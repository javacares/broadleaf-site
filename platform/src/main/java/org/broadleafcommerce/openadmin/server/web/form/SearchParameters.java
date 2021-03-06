package org.broadleafcommerce.openadmin.server.web.form;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.broadleafcommerce.common.util.dao.OrderByDirection;
import org.broadleafcommerce.common.util.dao.SearchMode;
import org.broadleafcommerce.common.util.dao.SearchTemplate;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Holds extra informations to be displayed and filled by the spring mvc {@link Controller}.
 */
public class SearchParameters implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(SearchParameters.class);

    public static final String ASCENDING = "ascending";
    public static final String DESCENDING = "descending";

    private int pageNumber = 1;
    private int pageSize = 50;
    private SearchMode searchMode = SearchMode.ANYWHERE;
    private String sortColumnKey;
    private String sortOrder = ASCENDING;
    private String searchPattern;
    private boolean caseSensitive;
    private String namedQuery;

    /**
     * Limit the search to the passed page number. Must be positive (starts at 1). Its default value is 1.
     */
    public void setPageNumber(int pageNumber) {
        if (pageNumber <= 0) {
            logger.warn("trying to access invalid page number: " + pageNumber);
            pageNumber = 1;
        }

        this.pageNumber = pageNumber;
    }

    /**
     * Returns the page number used to limit the search. It is always positive (starts at 1).
     */
    public int getPageNumber() {
        return this.pageNumber;
    }

    /**
     * Set the max number of items that can be displayed per page. The page size must be positive (starts at 1). It is used to limit the search.
     */
    public void setPageSize(int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        } else {
            logger.warn("trying to set an invalid page size: " + pageSize);
        }
    }

    /**
     * Return the max number of search items that can be displayed per page.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Set the search mode as a String.
     * 
     * @param searchMode
     */
    public void setSearchMode(SearchMode searchMode) {
        this.searchMode = searchMode;
    }

    public SearchMode getSearchMode() {
        return searchMode;
    }

    /**
     * Indicates that search result must be sort by the passed sortColumnKey.
     * 
     * @param sortColumnKey
     */
    public void setSortColumnKey(String sortColumnKey) {
        this.sortColumnKey = sortColumnKey;
    }

    /**
     * Returns the column name used to sort the search result.
     */
    public String getSortColumnKey() {
        return sortColumnKey;
    }

    public boolean hasSortColumnKey() {
        return StringUtils.isNotBlank(sortColumnKey);
    }

    /**
     * Set the sort order.
     * 
     * @param sortOrder either ASCENDING, DESCENDING or an empty String
     */
    public void setSortOrder(String sortOrder) {
        if (ASCENDING.equalsIgnoreCase(sortOrder) || DESCENDING.equalsIgnoreCase(sortOrder) || "".equals(sortOrder)) {
            this.sortOrder = sortOrder;
        }
    }

    /**
     * Return the sort order that is ASCENDING, DESCENDING or an empty String.
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * Return the Sort Order for the passed columns. It is used by the view.
     */
    public String getSortOrderFor(String sortColumnKey) {
        if (getSortColumnKey().equalsIgnoreCase(sortColumnKey)) {
            return getSortOrder();
        }

        return "";
    }

    /**
     * Return the reverse Sort Order for the passed columns. It is used by the view.
     * 
     * @return DESCENDING if sort order is ASCENDING; ASCENDING otherwise.
     */
    public String getReverseSortOrderFor(String sortColumnKey) {
        if (ASCENDING.equals(getSortOrderFor(sortColumnKey))) {
            return DESCENDING;
        }

        return ASCENDING;
    }

    /**
     * Set the search pattern to apply to all string fields for executing the search.
     * 
     * @param searchPattern
     */
    public void setSearchPattern(String searchPattern) {
        this.searchPattern = searchPattern;
    }

    /**
     * Returns the search pattern that must be applied to all string fields when executing the search.
     */
    public String getSearchPattern() {
        return searchPattern;
    }

    public boolean hasSearchPattern() {
        return StringUtils.isNotBlank(searchPattern);
    }

    /**
     * Set the case sensitiveness of the search. Its default value is false.
     * 
     * @param caseSensitive true for case sensitive search, false for case insensitive search.
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Return true for case sensitive search, false for case insensitive search.
     */
    public boolean getCaseSensitive() {
        return this.caseSensitive;
    }

    /**
     * Set the named query that must be used to perform the search.
     * 
     * @param namedQuery an hibernate named query.
     */
    public void setNamedQuery(String namedQuery) {
        this.namedQuery = namedQuery;
    }

    /**
     * Return the named query that must be used to perform the search.
     */
    public String getNamedQuery() {
        return namedQuery;
    }

    public boolean hasNamedQuery() {
        return StringUtils.isNotBlank(namedQuery);
    }
    
    /**
     * Convert this searchParameters instance to a SearchTemplate. Copy all the property and calculate the first and max results properties depending on the
     * pageSize and pageNumber properties of the passed searchParameters.
     * 
     * @return a searchTemplate to be used with a manager finder method.
     */
    public SearchTemplate toSearchTemplate() {
        SearchTemplate searchTemplate = new SearchTemplate();
        searchTemplate.setSearchMode(getSearchMode());
        searchTemplate.setNamedQuery(getNamedQuery());
        searchTemplate.setSearchPattern(getSearchPattern());
        searchTemplate.setCaseSensitive(getCaseSensitive());
        OrderByDirection direction = null;
        if (DESCENDING.equals(getSortOrder())) {
            direction = OrderByDirection.DESC;
        } else if (ASCENDING.equals(getSortOrder())) {
            direction = OrderByDirection.ASC;
        }
        if (getSortColumnKey() != null) {
            searchTemplate.addOrderBy(getSortColumnKey(), direction);
        }
        searchTemplate.setFirstResult(getPageSize() * (getPageNumber() - 1));
        searchTemplate.setMaxResults(getPageSize());
        return searchTemplate;
    }

}