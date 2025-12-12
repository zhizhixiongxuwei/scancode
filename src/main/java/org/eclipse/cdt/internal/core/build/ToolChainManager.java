/**
 * ****************************************************************************
 *  Copyright (c) 2015, 2019 QNX Software Systems and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 * *****************************************************************************
 */
package org.eclipse.cdt.internal.core.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.build.IToolChain;
import org.eclipse.cdt.core.build.IToolChainManager;
import org.eclipse.cdt.core.build.IToolChainProvider;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ToolChainManager implements IToolChainManager {

    public Map<String, IConfigurationElement> providerElements;

    public Map<String, IToolChainProvider> providers;

    public Map<String, Map<String, IToolChain>> toolChains;

    public Map<String, String> toolChainTypeNames = new HashMap<>();

    public List<IToolChain> orderedToolChains;

    private List<ISafeRunnable> listeners = new ArrayList<>();

    private synchronized void init() {
        if (providerElements == null) {
            providerElements = new HashMap<>();
            providers = new HashMap<>();
            // Load the types
            IExtensionRegistry registry = Platform.getExtensionRegistry();
            //$NON-NLS-1$
            IExtensionPoint typesPoint = registry.getExtensionPoint(CCorePlugin.PLUGIN_ID + ".toolChainProvider");
            for (IConfigurationElement element : typesPoint.getConfigurationElements()) {
                //$NON-NLS-1$
                String id = element.getAttribute("id");
                providerElements.put(id, element);
            }
            // Load the discovered toolchains
            toolChains = new HashMap<>();
            for (IConfigurationElement element : providerElements.values()) {
                switch(element.getName()) {
                    case //$NON-NLS-1$
                    "provider":
                        // TODO check for enablement
                        SafeRunner.run(() -> {
                            //$NON-NLS-1$
                            IToolChainProvider provider = (IToolChainProvider) element.createExecutableExtension("class");
                            //$NON-NLS-1$
                            providers.put(element.getAttribute("id"), provider);
                            provider.init(ToolChainManager.this);
                        });
                        break;
                    case //$NON-NLS-1$
                    "type":
                        //$NON-NLS-1$ //$NON-NLS-2$
                        toolChainTypeNames.put(element.getAttribute("id"), element.getAttribute("name"));
                        break;
                }
            }
            orderedToolChains = new ArrayList<>();
            Preferences prefs = InstanceScope.INSTANCE.getNode(CCorePlugin.PLUGIN_ID).node(getClass().getSimpleName()).node(//$NON-NLS-1$
            "order");
            //$NON-NLS-1$ //$NON-NLS-2$
            String nString = prefs.get("n", "");
            if (!nString.isEmpty()) {
                try {
                    int n = Integer.parseInt(nString);
                    for (int i = 0; i < n; ++i) {
                        //$NON-NLS-1$ //$NON-NLS-2$
                        String typeId = prefs.get(Integer.toString(i) + ".type", "");
                        //$NON-NLS-1$ //$NON-NLS-2$
                        String id = prefs.get(Integer.toString(i) + ".id", "");
                        IToolChain toolChain = getToolChain(typeId, id);
                        if (toolChain != null) {
                            orderedToolChains.add(toolChain);
                        }
                    }
                } catch (NumberFormatException e) {
                    CCorePlugin.log(e);
                } catch (CoreException e) {
                    CCorePlugin.log(e.getStatus());
                }
            }
            for (Map<String, IToolChain> type : toolChains.values()) {
                for (IToolChain toolChain : type.values()) {
                    if (!orderedToolChains.contains(toolChain)) {
                        orderedToolChains.add(toolChain);
                    }
                }
            }
        }
    }

    @Override
    public String getToolChainTypeName(String typeId) {
        init();
        String name = toolChainTypeNames.get(typeId);
        return name != null ? name : typeId;
    }

    private void saveToolChainOrder() {
        Preferences prefs = InstanceScope.INSTANCE.getNode(CCorePlugin.PLUGIN_ID).node(getClass().getSimpleName()).node(//$NON-NLS-1$
        "order");
        //$NON-NLS-1$
        prefs.put("n", Integer.toString(orderedToolChains.size()));
        int i = 0;
        for (IToolChain toolChain : orderedToolChains.toArray(new IToolChain[0])) {
            //$NON-NLS-1$
            prefs.put(Integer.toString(i) + ".type", toolChain.getTypeId());
            //$NON-NLS-1$
            prefs.put(Integer.toString(i) + ".id", toolChain.getId());
            i++;
        }
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            CCorePlugin.log(e);
        }
    }

    @Override
    public void addToolChain(IToolChain toolChain) {
        Map<String, IToolChain> type = toolChains.get(toolChain.getTypeId());
        if (type == null) {
            // use LinkedHashMap so input order is maintained
            type = new LinkedHashMap<>();
            toolChains.put(toolChain.getTypeId(), type);
        }
        type.put(toolChain.getId(), toolChain);
        if (orderedToolChains != null) {
            // is null at init time where order will be established later
            orderedToolChains.add(toolChain);
            saveToolChainOrder();
        }
        fireChange();
    }

    @Override
    public void removeToolChain(IToolChain toolChain) {
        Map<String, IToolChain> type = toolChains.get(toolChain.getTypeId());
        if (type != null) {
            type.remove(toolChain.getId());
        }
        if (orderedToolChains.remove(toolChain)) {
            saveToolChainOrder();
        }
        fireChange();
    }

    @Override
    public IToolChainProvider getProvider(String providerId) throws CoreException {
        init();
        IToolChainProvider provider = providers.get(providerId);
        if (provider == null) {
            IConfigurationElement element = providerElements.get(providerId);
            if (element != null) {
                SafeRunner.run(() -> {
                    //$NON-NLS-1$
                    IToolChainProvider provider2 = (IToolChainProvider) element.createExecutableExtension("class");
                    providers.put(providerId, provider2);
                    provider2.init(ToolChainManager.this);
                });
                return providers.get(providerId);
            }
        }
        return provider;
    }

    @Override
    public IToolChain getToolChain(String typeId, String id) throws CoreException {
        init();
        Map<String, IToolChain> type = toolChains.get(typeId);
        return type != null ? type.get(id) : null;
    }

    @Override
    public Collection<IToolChain> getToolChainsMatching(Map<String, String> properties) {
        init();
        List<IToolChain> tcs = new ArrayList<>();
        if (orderedToolChains != null) {
            for (IToolChain toolChain : orderedToolChains.toArray(new IToolChain[0])) {
                if (toolChain.matches(properties)) {
                    tcs.add(toolChain);
                }
            }
        }
        return tcs;
    }

    @Override
    public Collection<IToolChain> getAllToolChains() throws CoreException {
        init();
        return Collections.unmodifiableCollection(orderedToolChains);
    }

    @Override
    public void setToolChainOrder(List<IToolChain> orderedToolchains) throws CoreException {
        this.orderedToolChains = orderedToolchains;
        saveToolChainOrder();
    }

    @Override
    public void addToolChainListener(ISafeRunnable listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeToolChainListener(ISafeRunnable listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void fireChange() {
        List<ISafeRunnable> runners;
        synchronized (listeners) {
            runners = new ArrayList<>(listeners);
        }
        for (ISafeRunnable runner : runners) {
            SafeRunner.run(runner);
        }
    }
}
