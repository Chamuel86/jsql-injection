package com.test.vendor.cubrid;

import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Ignore;

import com.jsql.model.exception.InjectionFailureException;

@Ignore
public class CubridNormalGetTestSuite extends ConcreteCubridTestSuite {

    public CubridNormalGetTestSuite() throws ClassNotFoundException, SQLException {
        super();
    }

    @Override
    @BeforeClass
    public void setupInjection() throws InjectionFailureException {
//        InjectionModel model = new InjectionModel();
//        MediatorModel.register(model);
//        model.displayVersion();
//
//        MediatorModel.model().addObserver(new SystemOutTerminal());
//
//        ConnectionUtil.setUrlBase("http://"+ AbstractTestSuite.HOSTNAME +"/cubrid_simulate_get.php");
//        ParameterUtil.setQueryString(Arrays.asList(new SimpleEntry<String, String>("lib", "0")));
//        ConnectionUtil.setMethodInjection(MethodInjection.QUERY);
//
//        MediatorModel.model().beginInjection();
//
//        MediatorModel.model().setStrategy(StrategyInjection.NORMAL);
    }
}
