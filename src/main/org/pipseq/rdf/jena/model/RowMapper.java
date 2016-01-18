/*
 * Copyright © 2015 www.pipseq.org
 * @author rspates
 */
package org.pipseq.rdf.jena.model;

import java.util.List;

import com.hp.hpl.jena.query.ResultSet;

// TODO: Auto-generated Javadoc
/**
 * The Interface RowMapper.
 */
public interface RowMapper {

	/**
	 * Map row.
	 *
	 * @param rs the rs
	 * @param rowNum the row num
	 * @return the list
	 */
	List<Object> mapRow(ResultSet rs, int rowNum);

}
