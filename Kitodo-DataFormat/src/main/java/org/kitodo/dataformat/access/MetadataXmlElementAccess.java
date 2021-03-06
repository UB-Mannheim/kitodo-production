/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.dataformat.access;

import org.kitodo.api.MdSec;
import org.kitodo.api.MetadataEntry;
import org.kitodo.dataformat.metskitodo.MetadataType;

/**
 * A meta-data entry. A meta-data entry consists of a key and a value. The key
 * is stored in the superclass {@link MetadataXmlElementsAccess}. There is also the domain, that
 * is an indication in which container within the METS file the meta-data entry
 * is stored.
 */
public class MetadataXmlElementAccess extends MetadataXmlElementsAccess {
    /**
     * The data object of this meta-data XML element access.
     */
    private final MetadataEntry metadataEntry;

    /**
     * Public constructor for creating a new meta-data entry. This constructor
     * can be used with the service loader to create a new meta-data entry.
     */
    public MetadataXmlElementAccess() {
        metadataEntry = new MetadataEntry();
    }

    /**
     * Constructor for producing a meta-data entry from METS/Kitodo format.
     * 
     * @param domain
     *            at which place the entry was found
     * @param metadataType
     *            Kitodo meta-data object (input)
     */
    MetadataXmlElementAccess(MdSec domain, MetadataType metadataType) {
        this();
        metadataEntry.setDomain(domain);
        metadataEntry.setKey(metadataType.getName());
        metadataEntry.setValue(metadataType.getValue());
    }

    MetadataXmlElementAccess(MetadataEntry metadataEntry) {
        this.metadataEntry = metadataEntry;
    }

    MetadataEntry getMetadataEntry() {
        return metadataEntry;
    }

    /**
     * Creates a Kitodo XML element from the meta-data entry.
     * 
     * @return a Kitodo XML element
     */
    MetadataType toMetadata() {
        MetadataType metadataType = new MetadataType();
        metadataType.setName(metadataEntry.getKey());
        metadataType.setValue(metadataEntry.getValue());
        return metadataType;
    }
}
