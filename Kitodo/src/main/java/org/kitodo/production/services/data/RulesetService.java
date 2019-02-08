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

package org.kitodo.production.services.data;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.kitodo.config.ConfigCore;
import org.kitodo.config.enums.ParameterCore;
import org.kitodo.data.database.beans.Ruleset;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.data.database.persistence.RulesetDAO;
import org.kitodo.data.elasticsearch.index.Indexer;
import org.kitodo.data.elasticsearch.index.type.RulesetType;
import org.kitodo.data.elasticsearch.index.type.enums.DocketTypeField;
import org.kitodo.data.elasticsearch.index.type.enums.RulesetTypeField;
import org.kitodo.data.elasticsearch.search.Searcher;
import org.kitodo.data.exceptions.DataException;
import org.kitodo.production.dto.ClientDTO;
import org.kitodo.production.dto.RulesetDTO;
import org.kitodo.production.helper.metadata.legacytypeimplementations.LegacyPrefsHelper;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.production.services.data.base.TitleSearchService;
import org.primefaces.model.SortOrder;

public class RulesetService extends TitleSearchService<Ruleset, RulesetDTO, RulesetDAO> {

    private static final Logger logger = LogManager.getLogger(RulesetService.class);
    private static RulesetService instance = null;

    /**
     * Constructor with Searcher and Indexer assigning.
     */
    private RulesetService() {
        super(new RulesetDAO(), new RulesetType(), new Indexer<>(Ruleset.class), new Searcher(Ruleset.class));
    }

    /**
     * Return singleton variable of type RulesetService.
     *
     * @return unique instance of RulesetService
     */
    public static RulesetService getInstance() {
        if (Objects.equals(instance, null)) {
            synchronized (RulesetService.class) {
                if (Objects.equals(instance, null)) {
                    instance = new RulesetService();
                }
            }
        }
        return instance;
    }

    @Override
    public Long countDatabaseRows() throws DAOException {
        return countDatabaseRows("SELECT COUNT(*) FROM Ruleset");
    }

    @Override
    public Long countNotIndexedDatabaseRows() throws DAOException {
        return countDatabaseRows("SELECT COUNT(*) FROM Ruleset WHERE indexAction = 'INDEX' OR indexAction IS NULL");
    }

    @Override
    public Long countResults(Map filters) throws DataException {
        return countDocuments(getRulesetsForCurrentUserQuery());
    }

    @Override
    public List<RulesetDTO> loadData(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters)
            throws DataException {
        return findByQuery(getRulesetsForCurrentUserQuery(), getSortBuilder(sortField, sortOrder), first, pageSize,
            false);
    }

    @Override
    public List<Ruleset> getAllNotIndexed() {
        return getByQuery("FROM Ruleset WHERE indexAction = 'INDEX' OR indexAction IS NULL");
    }

    @Override
    public List<Ruleset> getAllForSelectedClient() {
        return dao.getByQuery("SELECT r FROM Ruleset AS r INNER JOIN r.client AS c WITH c.id = :clientId",
            Collections.singletonMap("clientId", ServiceManager.getUserService().getSessionClientId()));
    }

    @Override
    public RulesetDTO convertJSONObjectToDTO(Map<String, Object> jsonObject, boolean related) throws DataException {
        RulesetDTO rulesetDTO = new RulesetDTO();
        rulesetDTO.setId(getIdFromJSONObject(jsonObject));
        rulesetDTO.setTitle(RulesetTypeField.TITLE.getStringValue(jsonObject));
        rulesetDTO.setFile(RulesetTypeField.FILE.getStringValue(jsonObject));
        rulesetDTO.setOrderMetadataByRuleset(
            RulesetTypeField.ORDER_METADATA_BY_RULESET.getBooleanValue(jsonObject));

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(RulesetTypeField.CLIENT_ID.getIntValue(jsonObject));
        clientDTO.setName(RulesetTypeField.CLIENT_NAME.getStringValue(jsonObject));

        rulesetDTO.setClientDTO(clientDTO);
        return rulesetDTO;
    }

    /**
     * Get list of rulesets for given title.
     *
     * @param title
     *            for get from database
     * @return list of rulesets
     */
    public List<Ruleset> getByTitle(String title) {
        return dao.getByQuery("FROM Ruleset WHERE title = :title", Collections.singletonMap("title", title));
    }

    /**
     * Find ruleset with exact file.
     *
     * @param file
     *            of the searched ruleset
     * @return search result
     */
    public Map<String, Object> findByFile(String file) throws DataException {
        QueryBuilder queryBuilder = createSimpleQuery(RulesetTypeField.FILE.getKey(), file, true);
        return findDocument(queryBuilder);
    }

    /**
     * Find rulesets with exact file content.
     *
     * @param fileContent
     *            of the searched ruleset
     * @return list of JSON objects
     */
    public List<Map<String, Object>> findByFileContent(String fileContent) throws DataException {
        QueryBuilder queryBuilder = createSimpleQuery(RulesetTypeField.FILE_CONTENT.getKey(), fileContent, true);
        return findDocuments(queryBuilder);
    }

    /**
     * Find rulesets for client id.
     *
     * @param clientId
     *            of the searched rulesets
     * @return search result
     */
    List<Map<String, Object>> findByClientId(Integer clientId) throws DataException {
        QueryBuilder query = createSimpleQuery(DocketTypeField.CLIENT_ID.getKey(), clientId, true);
        return findDocuments(query);
    }

    /**
     * Find ruleset with exact title and file name.
     *
     * @param title
     *            of the searched ruleset
     * @param file
     *            of the searched ruleset
     * @return search result
     */
    public Map<String, Object> findByTitleAndFile(String title, String file) throws DataException {
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(createSimpleQuery(RulesetTypeField.TITLE.getKey(), title, true, Operator.AND));
        query.must(createSimpleQuery(RulesetTypeField.FILE.getKey(), file, true, Operator.AND));
        return findDocument(query);
    }

    /**
     * Find ruleset with exact title or file name.
     *
     * @param title
     *            of the searched ruleset
     * @param file
     *            of the searched ruleset
     * @return search result
     */
    public List<Map<String, Object>> findByTitleOrFile(String title, String file) throws DataException {
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.should(createSimpleQuery(RulesetTypeField.TITLE.getKey(), title, true));
        query.should(createSimpleQuery(RulesetTypeField.FILE.getKey(), file, true));
        return findDocuments(query);
    }

    /**
     * Get preferences.
     *
     * @param ruleset
     *            object
     * @return preferences
     */
    public LegacyPrefsHelper getPreferences(Ruleset ruleset) {
        LegacyPrefsHelper myPreferences = new LegacyPrefsHelper();
        try {
            myPreferences.loadPrefs(ConfigCore.getParameter(ParameterCore.DIR_RULESETS) + ruleset.getFile());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return myPreferences;
    }

    private QueryBuilder getRulesetsForCurrentUserQuery() {
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(createSimpleQuery(RulesetTypeField.CLIENT_ID.getKey(),
                ServiceManager.getUserService().getSessionClientId(), true));
        return query;
    }
}
