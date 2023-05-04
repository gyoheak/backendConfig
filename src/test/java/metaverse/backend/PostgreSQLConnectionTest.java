package metaverse.backend;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@SpringBootTest
public class PostgreSQLConnectionTest {
    private String URL = "jdbc:postgresql://localhost:5432/postgres";
    private String USERNAME = "admin"; //postgresql 계정
    private String PASSWORD = "tmax1234"; //비밀번호

    public void ConnectionTest() throws Exception{
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        System.out.println(conn);
        Statement pre = conn.createStatement();
        ResultSet rs = pre.executeQuery("select * from test");

        if (rs.next()) {
            System.out.println("rs = " + rs);
            System.out.println("userId = " + rs.getString("id"));
        }
    }
}
