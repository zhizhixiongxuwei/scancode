package org.eclipse.jdt.internal.core.search.indexing;

import java.nio.charset.Charset;
import org.eclipse.jdt.core.search.SearchDocument;

public class ManifestIndexer extends AbstractIndexer {

    //$NON-NLS-1$
    static final public String AUTOMATIC_MODULE_NAME = "Automatic-Module-Name";

    public ManifestIndexer(SearchDocument document) {
        super(document);
    }

    @Override
    public void indexDocument() {
        byte[] entry = this.document.getByteContents();
        String text = new String(entry, Charset.defaultCharset());
        //$NON-NLS-1$
        String[] kv = text.split(":");
        if (kv != null && kv.length > 1 && kv[0] != null && kv[1] != null) {
            if (kv[0].equals(AUTOMATIC_MODULE_NAME)) {
                addModuleDeclaration(kv[1].toCharArray());
            }
        }
    }
}
