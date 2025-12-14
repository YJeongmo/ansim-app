package com.ansimyoyang.repository;

import com.ansimyoyang.domain.Institution;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class InstitutionJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public InstitutionJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Institution> rowMapper = (rs, rowNum) -> Institution.builder()
            .institutionId(rs.getLong("institution_id"))
            .institutionName(rs.getString("name")) // DB 컬럼명은 'name'
            .address(rs.getString("address"))
            .phoneNumber(rs.getString("phone")) // DB 컬럼명은 'phone'
            .rating(rs.getObject("rating", BigDecimal.class))
            .build();

    public Optional<Institution> findById(Long id) {
        String sql = "SELECT * FROM institution WHERE institution_id = ?";
        return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
    }

    public List<Institution> findByName(String name) {
        String sql = "SELECT * FROM institution WHERE name LIKE ? ORDER BY name ASC";
        return jdbcTemplate.query(sql, rowMapper, "%" + name + "%");
    }

    public Optional<Institution> findByNameAndPhone(String name, String phone) {
        String sql = "SELECT * FROM institution WHERE name = ? AND phone = ?";
        return jdbcTemplate.query(sql, rowMapper, name, phone).stream().findFirst();
    }

    public List<Institution> findAll() {
        String sql = "SELECT * FROM institution ORDER BY name ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }
}
