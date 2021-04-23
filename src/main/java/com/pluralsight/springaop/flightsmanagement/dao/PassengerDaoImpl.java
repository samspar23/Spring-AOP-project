package com.pluralsight.springaop.flightsmanagement.dao;

import com.pluralsight.springaop.flightsmanagement.domain.Passenger;
import com.pluralsight.springaop.flightsmanagement.exceptions.CountryDoesNotExistException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PassengerDaoImpl implements PassengerDao {

    private static Map<Integer, Passenger> passengerMap = new HashMap<>();

    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private RowMapper<Passenger> rowMapper = (resultSet, rowNum) -> {
        Passenger passenger = new Passenger();
        passenger.setName(resultSet.getString("name"));
        passenger.setCountry(resultSet.getString("country"));
        return passenger;
    };

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    private Passenger getById(int id) {
        String query = "SELECT * FROM Passengers WHERE ID = ?";
        return jdbcTemplate.queryForObject(query, rowMapper, id);
    }

    @Override
    public Passenger getPassenger(int id) {
        if (passengerMap.containsKey(id)) {
            return passengerMap.get(id);
        }
        Passenger passenger = getById(id);
        return passenger;
    }

    @Override
    public void insert(Passenger passenger) {
        if (!Arrays.asList(Locale.getISOCountries()).contains(passenger.getCountry())) {
            throw new CountryDoesNotExistException(passenger.getCountry());
        }

        String query = "INSERT INTO Passengers (NAME, COUNTRY) VALUES (?, ?)";
        jdbcTemplate.update(query, new Object[] { passenger.getName(), passenger.getCountry() });
    }

    public static Map<Integer, Passenger> getPassengerMap() {
        return passengerMap;
    }
}
