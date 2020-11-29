package spring.rest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JdbcRestController {

    private static final String template = "Hello, s!";
    private final AtomicLong counter = new AtomicLong();
    private static final Logger LOGGER = Logger.getRootLogger();
    
    @RequestMapping("/greeting-altibase")
    public Greeting greetingAltibase(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("Altibase.jdbc.driver.AltibaseDriver");
        // docker run -it altibase/altibase

        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
            try (
                Connection con = DriverManager.getConnection("jdbc:Altibase://jsql-altibase:20300/mydb", "sys", "manager");
                PreparedStatement pstmt = con.prepareStatement("select db_name from SYSTEM_.SYS_DATABASE_ where '1' = '"+ inject +"'");
            ) {
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {
                    result.append(rs.getString(1));
                }
            }
        
        Greeting greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-ctreeace")
    public Greeting greetingCTreeAce(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        // c-treeACE-Express.windows.64bit.v11.5.1.64705.190310.ACE.msi
        Class.forName("ctree.jdbc.ctreeDriver");

        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
            try (
                Connection con = DriverManager.getConnection("jdbc:ctree://localhost:6597/ctreeSQL", "ADMIN", "ADMIN");
                PreparedStatement pstmt = con.prepareStatement("select tbl from systables where '1' = '"+ inject +"'");
            ) {
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {
                    result.append(rs.getString(1));
                }
            }
        
        Greeting greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-exasol")
    public Greeting greetingExasol(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.exasol.jdbc.EXADriver");
        // docker run --name exasoldb -p 8563:8563 --detach --privileged --stop-timeout 120  exasol/docker-db
        
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
                Connection con = DriverManager.getConnection("jdbc:exa:127.0.0.1:8563", "sys", "exasol");
                PreparedStatement pstmt = con.prepareStatement("select COLUMN_SCHEMA from EXA_SYS_COLUMNS where '1' = '"+ inject +"'");
                ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        }
        
        Greeting greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
                );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-ignite")
    public Greeting greetingIgnite(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("org.apache.ignite.IgniteJdbcThinDriver");
        // docker run -d -p 10800:10800 apacheignite/ignite
        
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1", "ignite", "ignite");
            PreparedStatement pstmt = con.prepareStatement("select 'name' from PUBLIC.STUDENT where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        }
        
        Greeting greeting = new Greeting(
            this.counter.getAndIncrement(),
            String.format(template, name)
            + StringEscapeUtils.unescapeJava(result.toString())
        );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-frontbase")
    public Greeting greetingFrontbase(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        /* FrontBase-8.2.18-WinNT.zip
         * sql92.exe
         * sql92#1> create database firstdb;
            sql92#2>    connect to firstdb user _system;
            Auto committing is on: SET COMMIT TRUE;
            firstdb@localhost#3>    create user test;
            firstdb@localhost#4>    commit;
            jdbc:FrontBase://127.0.0.1/firstdb
            _system
            Service FBExec
            Service FrontBase firstdb
         */
        Class.forName("com.frontbase.jdbc.FBJDriver");
        
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
                Connection con = DriverManager.getConnection("jdbc:FrontBase://127.0.0.1/firstdb", "_system", "");
                PreparedStatement pstmt = con.prepareStatement("select \"SCHEMA_NAME\" from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'");
                ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        }
        
        Greeting greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
                );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-iris")
    public Greeting greetingIris(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.intersystems.jdbc.IRISDriver");
        
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
                Connection con = DriverManager.getConnection("jdbc:IRIS://127.0.0.1:1972/USER", "_SYSTEM", "Mw7SUqLPFbZWUu4");
                PreparedStatement pstmt = con.prepareStatement("select SCHEMA_NAME from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'");
                ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        }
        
        Greeting greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
                );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-monetdb")
    public Greeting greetingMonetDB(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("nl.cwi.monetdb.jdbc.MonetDriver");
        
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
                Connection con = DriverManager.getConnection("jdbc:monetdb://127.0.0.1:50001/db", "monetdb", "monetdb");
                PreparedStatement pstmt = con.prepareStatement("select name from schemas where '1' = '"+ inject +"'");
                ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        }
        
        Greeting greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
                );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-mimersql")
    public Greeting greetingMimerSQL(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.mimer.jdbc.Driver");
        
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
                Connection con = DriverManager.getConnection("jdbc:mimer://127.0.0.1:1360/mimerdb", "SYSADM", "SYSADM");
                PreparedStatement pstmt = con.prepareStatement("select schema_name from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'");
                ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        }
        
        Greeting greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
                );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-presto")
    public Greeting greetingPresto(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
                Connection con = DriverManager.getConnection("jdbc:presto://127.0.0.1:8078/system", "test", "");
                PreparedStatement pstmt = con.prepareStatement("select schema_name from INFORMATION_SCHEMA.SCHEMATA where '1' = '"+ inject +"'");
                ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
        }
        
        Greeting greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
                );
        
        return greeting;
    }
    
    @RequestMapping("/greeting-firebird")
    public Greeting greetingFirebird(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        // Service Firebird Server - DefaultInstance
        Class.forName("org.firebirdsql.jdbc.FBDriver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:firebirdsql://127.0.0.1:3050/E:/Dev/Firebird/Firebird-2.5.9.27139-0_x64/examples/empbuild/EMPLOYEE.FDB", "sysdba", "masterkey");
            PreparedStatement pstmt = con.prepareStatement("select rdb$get_context('SYSTEM', 'DB_NAME') from rdb$database where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            
            greeting = new Greeting(
                this.counter.getAndIncrement(),
                String.format(template, name)
                + StringEscapeUtils.unescapeJava(result.toString())
            );
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/greeting-netezza")
    public Greeting greetingNetezza(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("org.netezza.Driver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:netezza://127.0.0.1:5480/SYSTEM", "admin", "password");
            PreparedStatement pstmt = con.prepareStatement("select schema_name from schemata where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            
            greeting = new Greeting(
                    this.counter.getAndIncrement(),
                    String.format(template, name)
                    + StringEscapeUtils.unescapeJava(result.toString())
                    );
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }
    
    @RequestMapping("/greeting-oracle")
    public Greeting greetingOracle(@RequestParam(value="name", defaultValue="World") String name) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.OracleDriver");
        
        Greeting greeting = null;
        String inject = name.replace(":", "\\:");
        StringBuilder result = new StringBuilder();
        
        try (
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:11521:ORCLCDB", "system", "Password1_One");
            PreparedStatement pstmt = con.prepareStatement("select distinct owner from all_tables where '1' = '"+ inject +"'");
        ) {
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                result.append(rs.getString(1));
            }
            
            greeting = new Greeting(
                    this.counter.getAndIncrement(),
                    String.format(template, name)
                    + StringEscapeUtils.unescapeJava(result.toString())
                    );
        } catch (Exception e) {
            
            greeting = this.initializeErrorMessage(e);
        }
        
        return greeting;
    }

    private Greeting initializeErrorMessage(Exception e) {
        
        String stacktrace = ExceptionUtils.getStackTrace(e);
        
        LOGGER.debug(stacktrace);
        
        Greeting greeting = new Greeting(
            0,
            template+"#"
            + StringEscapeUtils.unescapeJava(stacktrace)
        );
        
        return greeting;
    }
}