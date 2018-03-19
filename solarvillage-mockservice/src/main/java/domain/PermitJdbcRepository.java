package domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import domain.model.GovernmentPermit;

@Repository
public class PermitJdbcRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	class PermitRowMapper implements RowMapper<GovernmentPermit> {
		
		public GovernmentPermit mapRow(ResultSet rs, int rowNum) throws SQLException {
			GovernmentPermit permit = new GovernmentPermit();
			permit.setId(rs.getLong("id"));
			permit.setElectricalPermit(rs.getString("electricalpermit"));
			permit.setStructuralPermit(rs.getString("structuralpermit"));
			return permit;
		}
	}
	
	public List<GovernmentPermit> findAll() {
		return jdbcTemplate.query("select * from permit", new PermitRowMapper());
	}
	
	public GovernmentPermit findById(long id) {
		return jdbcTemplate.queryForObject("select * from permit where id=?", new Object[] { id },
				new BeanPropertyRowMapper<GovernmentPermit>(GovernmentPermit.class));
	}
	
	public String getElectricStatusById(long id) {
		GovernmentPermit permit = findById(id);
		String status = permit.getElectricalPermit();
		return status;
	}
	
	public String getStructuralStatusById(long id) {
		GovernmentPermit permit = findById(id);
		String status = permit.getStructuralPermit();
		return status;
	}

	public int deleteById(long id) {
		return jdbcTemplate.update("delete from permit where id=?", new Object[] { id });
	}

	public int insert(GovernmentPermit permit) {
		return jdbcTemplate.update("insert into permit (id, electricalpermit, structuralpermit) " + "values(?,  ?, ?)",
				new Object[] { permit.getId(), permit.getElectricalPermit(), permit.getStructuralPermit() });
	}

	public int update(GovernmentPermit permit) {
		return jdbcTemplate.update("update permit " + " set electricalpermit = ?, structuralpermit = ? " + " where id = ?",
				new Object[] { permit.getElectricalPermit(), permit.getStructuralPermit(), permit.getId() });
	}
	
	public int updateElectrical(GovernmentPermit permit) {
		return jdbcTemplate.update("update permit " + " set electricalpermit = ?" + " where id = ?",
				new Object[] { permit.getElectricalPermit(), permit.getId() });
	}
	
	public int updateStructural(GovernmentPermit permit) {
		return jdbcTemplate.update("update permit " + " set structuralpermit = ? " + " where id = ?",
				new Object[] { permit.getStructuralPermit(), permit.getId() });
	}
	
	public int rescindById(long id) {
		return jdbcTemplate.update("update permit " + " set electricalpermit = 'DENIED', structuralpermit = 'DENIED' " + " where id = ?",
				new Object[] { id });
	}
}
