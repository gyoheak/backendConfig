package metaverse.backend.basic;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import metaverse.backend.environment.EmailConfig;
import metaverse.backend.environment.PostgreSqlConfig;
import metaverse.backend.environment.QueryDSLConfig;
import metaverse.backend.model.MailTest;
import metaverse.backend.model.QTest;
import metaverse.backend.model.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

    @Autowired
    private PostgreSqlConfig postgreSqlConfig;

    @Autowired
    private QueryDSLConfig queryDSLConfig;

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private final String KEY = "keyword";

    @Autowired
    private MinioClient minioClient;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("HelloServlet.service");
        System.out.println("request = " + request);
        System.out.println("response = " + response);

        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write("안녕?");

        try {
            Connection connection = postgreSqlConfig.getPostgreSqlConnection();
            System.out.println(connection);
            Statement pre = connection.createStatement();
            ResultSet rs = pre.executeQuery("select * from test");

            if (rs.next()) {
                System.out.println("rs = " + rs);
                System.out.println("userId = " + rs.getString("id"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JPAQueryFactory queryFactory = queryDSLConfig.jpaQueryFactory();
//
        List<Test> list = queryFactory.selectFrom(QTest.test).fetch();
        list.forEach((value) -> {
            System.out.println("no = " + value.getNo());
            System.out.println("value.getId() = " + value.getId());
            System.out.println("value.getPw() = " + value.getPw());
        });

        MailTest mailTest = new MailTest();
        mailTest.setAddress("jeonghwan_kim5@tmax.co.kr");
        mailTest.setTitle("Spring Mail Test");
        mailTest.setMessage("Spring Mail Test Body");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailTest.getAddress());
        message.setSubject(mailTest.getTitle());
        message.setText(mailTest.getMessage());

        emailConfig.getJavaMailSender().send(message);

        //given
        String keyword = "mx";
        String keyword2 = "mv1-1";

        //when
        stringRedisTemplate.opsForZSet().add(KEY, keyword, 1);
        stringRedisTemplate.opsForZSet().incrementScore(KEY, keyword, 1);
        stringRedisTemplate.opsForZSet().incrementScore(KEY, keyword, 1);

        stringRedisTemplate.opsForZSet().add(KEY, keyword2, 1);

        //then
        System.out.println(stringRedisTemplate.opsForZSet().popMax(KEY));
        System.out.println(stringRedisTemplate.opsForZSet().popMin(KEY));

        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("test").build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("test").build());
            } else {
                System.out.println("Already Exists");
            }

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("test")
                            .object("test.jpg")
                            .filename("C:\\Users\\2023173\\Desktop\\Mx_Erd.jpg")
                            .build()
            );

        } catch (Exception e) {
            System.out.println("File Upload Error:" + e);
            e.printStackTrace();
        }
    }
}
