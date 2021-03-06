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

package org.kitodo.data.database.persistence;

import java.util.Collections;
import java.util.List;

import org.kitodo.data.database.beans.Comment;
import org.kitodo.data.database.beans.Process;
import org.kitodo.data.database.exceptions.DAOException;

public class CommentDAO extends BaseDAO<Comment> {

    @Override
    public Comment getById(Integer commentId) throws DAOException {
        Comment result = retrieveObject(Comment.class, commentId);
        if (result == null) {
            throw new DAOException("Object can not be found in database");
        }
        return result;
    }

    @Override
    public List<Comment> getAll() throws DAOException {
        return retrieveAllObjects(Comment.class);
    }

    @Override
    public List<Comment> getAll(int offset, int size) throws DAOException {
        return retrieveObjects("FROM Comment ORDER BY id ASC", offset, size);
    }

    @Override
    public List<Comment> getAllNotIndexed(int offset, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Integer commentId) throws DAOException {
        removeObject(Comment.class, commentId);
    }

    public List<Comment> getAllByProcess(Process process) {
        return getByQuery("FROM Comment WHERE process_id = :processId ORDER BY id ASC",
                Collections.singletonMap("processId", process.getId()));
    }

    /**
     * Save list of comments.
     *
     * @param list
     *            of commenss
     * @throws DAOException
     *             an exception that can be thrown from the underlying saveList()
     *             procedure failure.
     */
    public void saveList(List<Comment> list) throws DAOException {
        storeList(list);
    }
}
