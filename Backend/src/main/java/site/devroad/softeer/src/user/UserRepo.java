package site.devroad.softeer.src.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.exceptions.ExceptionType;
import site.devroad.softeer.src.exam.dto.domain.PeerDetail;
import site.devroad.softeer.src.user.dto.domain.UserDetail;
import site.devroad.softeer.src.user.model.Account;
import site.devroad.softeer.src.user.model.LoginInfo;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = false)
public class UserRepo {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepo(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public LoginInfo createLoginInfo(String email, String password, Long accountId) {
        jdbcTemplate.update("INSERT INTO LoginInfo (email, password, account_id) VALUES (?, ?, ?)",
                email, password, accountId);
        return jdbcTemplate.queryForObject("SELECT * FROM LoginInfo WHERE email = ?", loginInfoRowMapper(), email);
    }

    public Account createAccountInfo(String name, String phone, String type) {
        jdbcTemplate.update("INSERT INTO Account (name, phone, type) VALUES (?, ?, ?)",
                name, phone, type);
        return jdbcTemplate.queryForObject("SELECT * FROM Account WHERE phone = ?", accountRowMapper(), phone);
    }


    public Optional<Account> findAccountById(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM Account WHERE id = ?"
                    , accountRowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    public Optional<LoginInfo> findLoginInfoByEmail(String email) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM LoginInfo WHERE email = ?", loginInfoRowMapper(), email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<UserDetail> findAllUser() {
        try {
            return jdbcTemplate.query("SELECT * FROM LoginInfo l JOIN Account a " +
                    "ON l.account_id = a.id AND a.type = 'Student'", allUserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public List<LoginInfo> findNoRoadmapUser() {
        try {
            return jdbcTemplate.query("SELECT l.* FROM LoginInfo l JOIN Account a " +
                    "ON l.account_id = a.id AND a.roadmap_id IS NULL", loginInfoRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public Optional<Account> findByPhone(String phone) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM Account WHERE phone = ?", accountRowMapper(), phone));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Boolean isUserSubscribed(Long accountId) throws CustomException {
        try {
            Timestamp ends = jdbcTemplate.queryForObject(
                    "select end_at from Subscribe where account_id = ?", Timestamp.class, accountId
            );
            return ends.after(new Date());
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }


    public void setRoadmap(Long id, Long roadmapId) {
        jdbcTemplate.update("UPDATE Account SET roadmap_id = ? WHERE id=?", roadmapId, id);
    }

    public void doSubscribe(Long accountId) {
        jdbcTemplate.update("INSERT Subscribe(account_id) VALUES (?)", accountId);
        extendSubscribeEndDate(accountId, 31);
    }

    public void extendSubscribeEndDate(Long accountId, Integer date) {
        try {
            jdbcTemplate.update("UPDATE Subscribe \n" +
                    "SET end_at = ADDDATE(IF(NOW() > end_at, NOW(), end_at), INTERVAL ? DAY)\n" +
                    "where account_id = ?", date, accountId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new CustomException(ExceptionType.DATABASE_ERROR);
        }
    }

    public List<PeerDetail> findPeerDetailByExamId(Long examId) {
        return jdbcTemplate.query(
                "SELECT es.url,es.account_id,a.name ,s.name as \"curSubjectName\" \n" +
                        "from ExamSubmission es \n" +
                        "join Account a On a.id = es.account_id \n" +
                        "JOIN Roadmap r On r.id = a.roadmap_id  \n" +
                        "JOIN Chapter c2 On c2.id = r.chapter_id \n" +
                        "JOIN Course c On c.id = c2.course_id \n" +
                        "JOIN Subject s On s.id =c.subject_id \n" +
                        "WHERE es.is_passed=4 and es.exam_id = ? ", accountIdRowMapper(), examId);
    }

    private RowMapper<PeerDetail> accountIdRowMapper() {
        return ((rs, rowNum) -> {
            String url = (rs.getString("url"));
            String userName = (rs.getString("name"));
            String curSubject = (rs.getString("curSubjectName"));
            return new PeerDetail(url, userName, curSubject);
        });
    }

    private RowMapper<LoginInfo> loginInfoRowMapper() {
        return ((rs, rowNum) -> {
            Long id = (rs.getLong("id"));
            String email = (rs.getString("email"));
            String password = (rs.getString("password"));
            Long accountId = (rs.getLong("account_id"));
            return new LoginInfo(id, email, password, accountId);
        });
    }

    private RowMapper<UserDetail> allUserRowMapper() {
        return ((rs, rowNum) -> {
            Long id = (rs.getLong("account_id"));
            String email = (rs.getString("email"));
            Long roadmapId = (rs.getLong("roadmap_id"));
            String userName = (rs.getString("name"));
            return new UserDetail(id, email, roadmapId, userName);
        });
    }

    private RowMapper<Account> accountRowMapper() {
        return ((rs, rowNum) -> {
            Long id = (rs.getLong("id"));
            String name = (rs.getString("name"));
            Long roadMapId = (rs.getLong("roadmap_id"));
            String phone = (rs.getString("phone"));
            String type = (rs.getString("type"));
            Timestamp createdAt = (rs.getTimestamp("created_at"));
            Timestamp updatedAt = (rs.getTimestamp("updated_at"));
            return new Account(id, name, roadMapId, phone, type, createdAt, updatedAt);
        });
    }

}
