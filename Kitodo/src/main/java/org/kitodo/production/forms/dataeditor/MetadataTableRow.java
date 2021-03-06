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

package org.kitodo.production.forms.dataeditor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;

import org.apache.commons.lang3.tuple.Pair;
import org.kitodo.api.MdSec;
import org.kitodo.api.Metadata;
import org.kitodo.api.dataeditor.rulesetmanagement.Domain;
import org.kitodo.api.dataformat.IncludedStructuralElement;
import org.kitodo.production.helper.Helper;

abstract class MetadataTableRow implements Serializable {

    /**
     * Describes the relationship between the domain in the rule set and the
     * mdSec in the METS.
     */
    protected static final EnumMap<Domain, MdSec> DOMAIN_TO_MDSEC = new EnumMap<>(Domain.class);

    static {
        DOMAIN_TO_MDSEC.put(Domain.DESCRIPTION, MdSec.DMD_SEC);
        DOMAIN_TO_MDSEC.put(Domain.DIGITAL_PROVENANCE, MdSec.DIGIPROV_MD);
        DOMAIN_TO_MDSEC.put(Domain.RIGHTS, MdSec.RIGHTS_MD);
        DOMAIN_TO_MDSEC.put(Domain.SOURCE, MdSec.SOURCE_MD);
        DOMAIN_TO_MDSEC.put(Domain.TECHNICAL, MdSec.TECH_MD);
    }

    /**
     * The label of this row.
     */
    protected final String label;

    /**
     * Meta-data panel on which this row is showing.
     */
    protected final MetadataPanel panel;

    /**
     * Parental meta-data group.
     */
    private FieldedMetadataTableRow container;

    /**
     * Creates a new meta-data panel row.
     *
     * @param panel
     *            meta-data panel on which this row is showing
     * @param container
     *            parental meta-data group
     * @param label
     *            the label of this row
     */
    MetadataTableRow(MetadataPanel panel, FieldedMetadataTableRow container, String label) {
        this.panel = panel;
        this.container = container;
        this.label = label;
    }

    /**
     * This method is triggered when the user clicks the copy meta-data button.
     */
    public void copyClick() {
        try {
            panel.getClipboard().addAll(this.getMetadata());
        } catch (InvalidMetadataValueException e) {
            Helper.setErrorMessage(e.getLocalizedMessage());
        }
        Helper.getTranslation("dataEditor.copy", Collections.singletonList(Integer.toString(panel.getClipboard().size())));
    }

    /**
     * This method is triggered when the user clicks the delete meta-data
     * button.
     */
    public void deleteClick() {
        container.remove(this);
    }

    /**
     * Returns the type of input to be rendered in this row. One of the
     * following values:
     *
     * <p>
     * <table border=2>
     * <tr>
     * <th>Return value</th>
     * <th>input component</th>
     * <th>data object</th>
     * <th>properties</th>
     * </tr>
     * <tr>
     * <td>inputText</td>
     * <td>InputText</td>
     * <td rowspan=4>{@linkplain TextMetadataPanelRow}</td>
     * <td rowspan=4>String label (r/o)**<br>
     * boolean undefined (r/o)**<br>
     * boolean editable (r/o)*<br>
     * Validator validator (r/o)*<br>
     * String value (r/w)</td>
     * </tr>
     * <tr>
     * <td>inputTextarea</td>
     * <td>InputTextarea</td>
     * </tr>
     * <tr>
     * <td>spinner</td>
     * <td>Spinner</td>
     * </tr>
     * <tr>
     * <td>calendar</td>
     * <td>Calendar</td>
     * </tr>
     * <tr>
     * <td>dataTable</td>
     * <td>DataTable</td>
     * <td>{@linkplain FieldedMetadataPanelRow}</td>
     * <td>String label (r/o)**<br>
     * boolean undefined (r/o)**<br>
     * List&lt;MetadataTableRow> rows (r/o)</td>
     * </tr>
     * <tr>
     * <td>manyMenu</td>
     * <td>SelectManyMenu</td>
     * <td rowspan=3>{@linkplain SelectMetadataPanelRow}</td>
     * <td rowspan=3>String label (r/o)**<br>
     * boolean undefined (r/o)**<br>
     * boolean editable (r/o)*<br>
     * Validator validator (r/o)*<br>
     * List&lt;SelectItem> items (r/o)<br>
     * String selectedItem (r/w)<br>
     * List&lt;String> selectedItems (r/w)</td>
     * </tr>
     * <tr>
     * <td>oneMenu</td>
     * <td>SelectOneMenu</td>
     * </tr>
     * <tr>
     * <td>oneRadio</td>
     * <td>SelectOneRadio</td>
     * </tr>
     * <tr>
     * <td>toggleSwitch</td>
     * <td>ToggleSwitch</td>
     * <td>{@linkplain BooleanMetadataPanelRow}</td>
     * <td>String label (r/o)**<br>
     * boolean undefined (r/o)**<br>
     * boolean editable (r/o)*<br>
     * Validator validator (r/o)*<br>
     * boolean on (r/w)</td>
     * </tr>
     * <tfoot>
     * <tr>
     * <td colspan=4>** inherited from MetadataPanelRow (this class)<br>
     * * inherited from {@linkplain SimpleMetadataPanelRow}</td>
     * </tr>
     * </tfoot>
     * </table>
     *
     * @return the type of input to be rendered
     */
    public abstract String getInput();

    /**
     * Returns the label of this row.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the meta-data from this row, as far as it has to be stored in the
     * collection obtainable from {@link IncludedStructuralElement#getMetadata()}.
     *
     * @return the meta-data from this row
     * @throws InvalidMetadataValueException
     *             if the meta-data form contains syntactically wrong input
     */
    abstract Collection<Metadata> getMetadata() throws InvalidMetadataValueException;

    /**
     * If the meta-data entry addresses a property of the structure, returns a
     * pair of the setter and the value to set; else {@code null}. This method
     * it to be called when saving the data.
     *
     * @return if data is to be written a pair of the setter of the
     *         {@link IncludedStructuralElement} and the value to set, else null
     * @throws InvalidMetadataValueException
     *             if the meta-data form contains syntactically wrong input
     * @throws NoSuchMetadataFieldException
     *             if the field configured in the rule set does not exist
     */
    abstract Pair<Method, Object> getStructureFieldValue()
            throws InvalidMetadataValueException, NoSuchMetadataFieldException;

    /**
     * Returns if the field is not defined by the rule set. The front-end should
     * show some kind of warning sign then.
     *
     * @return if the field is not defined by the rule set
     */
    public abstract boolean isUndefined();
}
