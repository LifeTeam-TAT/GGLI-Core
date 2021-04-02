// package org.ace.insurance.product.testcase;
//
// import java.util.HashMap;
// import java.util.Map;
//
// import org.ace.insurance.common.ProductIDConfig;
// import org.ace.insurance.common.KeyFactorType;
// import org.ace.insurance.product.Product;
// import org.ace.insurance.product.service.interfaces.IProductService;
// import org.ace.insurance.system.common.buildingOccupation.BuildingOccupation;
// import
// org.ace.insurance.system.common.buildingOccupation.service.interfaces.IBuildingOccupationService;
// import org.ace.insurance.system.common.keyfactor.KeyFactor;
// import org.apache.log4j.Logger;
// import org.junit.AfterClass;
// import org.junit.BeforeClass;
// import org.junit.Test;
// import org.springframework.beans.factory.BeanFactory;
// import org.springframework.context.ApplicationContext;
// import org.springframework.context.support.ClassPathXmlApplicationContext;
//
// public class ProductServiceTest {
// private static Logger logger = Logger.getLogger(ProductServiceTest.class);
// private static IProductService productService;
// private static IBuildingOccupationService buildingOccupationService;
// private static final String OCCUPATION_ENTITY_NAME = "BuildingOccupation";
// private static final String BUILDINGCLASS_ENTITY_NAME = "BuildingClass";
// private static final String AGE_ENTITY_NAME = "AGE";
// private static final String TERM_ENTITY_NAME = "TERM";
// private static final String SUMINSURED_ENTITY_NAME = "SUM INSURED";
// private static final String CC_ENTITY_NAME = "CUBIC CAPACITY";
// private static final String SEAT_ENTITY_NAME = "NUMBER OF SEATS";
//
// @BeforeClass
// public static void init() {
// logger.info("MotorProposalServiceTestCase is
// started.........................................");
// ApplicationContext context = new
// ClassPathXmlApplicationContext("spring-beans.xml");
// BeanFactory factory = context;
// productService = (IProductService) factory.getBean("ProductService");
// buildingOccupationService = (IBuildingOccupationService)
// factory.getBean("BuildingOccupationService");
// logger.info("MotorProposalServiceTestCase instance has been loaded.");
//
// }
//
// @AfterClass
// public static void finished() {
// logger.info("MotorProposalServiceTestCase has been
// finished.........................................");
// }
//
// public static void main(String[] args) {
// org.junit.runner.JUnitCore.main(ProductServiceTest.class.getName());
// // Date dob = Utils.createDate(1983, 9, 26);
// // System.out.println(Utils.getAgeForNextYear(dob));
// }
//
// @Test
// public void findMotorPremiumRate() {
// Map<KeyFactor, String> keyFactorMap = new HashMap<KeyFactor, String>();
// // private car = ISPRD0030001000000000129032013
// // commercial car = ISPRD0030001000000000329032013
// Product product =
// productService.findProductById("ISPRD0030001000000000329032013");
//
// KeyFactor sunInsuredKF = new KeyFactor();
// sunInsuredKF.setId(ProductIDConfig.getSumInsuredId());
// sunInsuredKF.setKeyFactorType(KeyFactorType.FROM_TO);
// sunInsuredKF.setValue(SUMINSURED_ENTITY_NAME);
//
// KeyFactor ccKF = new KeyFactor();
// // CUBIC_CAPACITY = ISSYS0130001000000000429032013
// ccKF.setId("ISSYS0130001000000000429032013");
// ccKF.setKeyFactorType(KeyFactorType.FROM_TO);
// ccKF.setValue(CC_ENTITY_NAME);
//
// KeyFactor seatingKF = new KeyFactor();
// // SEATING = ISSYS0130001000000000329032013
// seatingKF.setId("ISSYS0130001000000000329032013");
// seatingKF.setKeyFactorType(KeyFactorType.FROM_TO);
// seatingKF.setValue(SEAT_ENTITY_NAME);
//
// KeyFactor loadingKF = new KeyFactor();
// // LOADING = ISSYS0130001000000000229032013
// seatingKF.setId("ISSYS0130001000000000229032013");
// seatingKF.setKeyFactorType(KeyFactorType.FROM_TO);
// seatingKF.setValue(SEAT_ENTITY_NAME);
//
// keyFactorMap.put(ccKF, "1500");
// keyFactorMap.put(seatingKF, "5");
// keyFactorMap.put(sunInsuredKF, "15000000");
// double enPrmRate = productService.findProductPremiumRate(keyFactorMap,
// product);
// logger.debug(" ^^^^ Motor premium rate : " + enPrmRate);
//
// }
//
// // @Test
// public void findLifeGroupPremiumRate() {
// Map<KeyFactor, String> keyFactorMap = new HashMap<KeyFactor, String>();
// // public life = ISPRD0030001000000002131032013
// // group = ISPRD0030001000000002431032013
// Product product =
// productService.findProductById("ISPRD0030001000000002431032013");
//
// KeyFactor sunInsuredKF = new KeyFactor();
// sunInsuredKF.setId(ProductIDConfig.getSumInsuredId());
// sunInsuredKF.setValue(SUMINSURED_ENTITY_NAME);
// sunInsuredKF.setKeyFactorType(KeyFactorType.FROM_TO);
//
// KeyFactor ageKF = new KeyFactor();
// // AGE = ISSYS0130001000000000729032013
// ageKF.setId("ISSYS0130001000000000729032013");
// ageKF.setKeyFactorType(KeyFactorType.FIXED);
// ageKF.setValue(AGE_ENTITY_NAME);
//
// KeyFactor termKF = new KeyFactor();
// // TERM = ISSYS0130001000000000829032013
// termKF.setId("ISSYS0130001000000000829032013");
// termKF.setKeyFactorType(KeyFactorType.FIXED);
// termKF.setValue(TERM_ENTITY_NAME);
//
// keyFactorMap.put(termKF, "1");
// keyFactorMap.put(sunInsuredKF, "30000000");
// double enPrmRate = productService.findProductPremiumRate(keyFactorMap,
// product);
// logger.debug(" ^^^^Life premium rate : " + enPrmRate);
//
// }
//
// // @Test
// public void findLifePublicPremiumRate() {
// Map<KeyFactor, String> keyFactorMap = new HashMap<KeyFactor, String>();
// // public life = ISPRD0030001000000002131032013
// // group = ISPRD0030001000000002431032013
// Product product =
// productService.findProductById("ISPRD0030001000000002131032013");
//
// KeyFactor sunInsuredKF = new KeyFactor();
// sunInsuredKF.setId(ProductIDConfig.getSumInsuredId());
// sunInsuredKF.setValue(SUMINSURED_ENTITY_NAME);
// sunInsuredKF.setKeyFactorType(KeyFactorType.FROM_TO);
//
// KeyFactor ageKF = new KeyFactor();
// // AGE = ISSYS0130001000000000729032013
// ageKF.setId("ISSYS0130001000000000729032013");
// ageKF.setKeyFactorType(KeyFactorType.FIXED);
// ageKF.setValue(AGE_ENTITY_NAME);
//
// KeyFactor termKF = new KeyFactor();
// // TERM = ISSYS0130001000000000829032013
// termKF.setId("ISSYS0130001000000000829032013");
// termKF.setKeyFactorType(KeyFactorType.FIXED);
// termKF.setValue(TERM_ENTITY_NAME);
//
// keyFactorMap.put(ageKF, "33");
// keyFactorMap.put(termKF, "10");
// keyFactorMap.put(sunInsuredKF, "30000000");
// double enPrmRate = productService.findProductPremiumRate(keyFactorMap,
// product);
// logger.debug(" ^^^^Life premium rate : " + enPrmRate);
//
// }
//
// // @Test
// public void findFirePremiumRate() {
// Map<KeyFactor, String> keyFactorMap = new HashMap<KeyFactor, String>();
// // Fire Declaration Policy Product ID = ISPRD003HO000000002111072013
// // Building = ISPRD0030001000000001731032013
// Product product =
// productService.findProductById("ISPRD0030001000000001731032013");
// // First class ID = ISSYS0300001000000000131032013
// // Dwelling House / Apartment = ISSYS0110001000000000131032013 #0.2
// // FACTORIES - Animal Food Manufacturing =
// // ISSYS0110001000000005431032013 #0.56
// BuildingOccupation buildingOccupation =
// buildingOccupationService.findBuildingOccupationById("ISSYS0110001000000005431032013");
//
// KeyFactor occupationKF = new KeyFactor();
// occupationKF.setId(ProductIDConfig.getBuildingOccupationId());
// occupationKF.setKeyFactorType(KeyFactorType.REFERENCE);
// occupationKF.setValue(OCCUPATION_ENTITY_NAME);
//
// KeyFactor buildingClassKF = new KeyFactor();
// buildingClassKF.setId(ProductIDConfig.getBuildingClassId());
// buildingClassKF.setKeyFactorType(KeyFactorType.REFERENCE);
// buildingClassKF.setValue(BUILDINGCLASS_ENTITY_NAME);
//
// KeyFactor sunInsuredKF = new KeyFactor();
// sunInsuredKF.setId(ProductIDConfig.getSumInsuredId());
// sunInsuredKF.setKeyFactorType(KeyFactorType.FROM_TO);
//
// keyFactorMap.put(occupationKF, buildingOccupation.getId());
// keyFactorMap.put(sunInsuredKF, product.getBaseSumInsured() + "");
// double enPrmRate = productService.findProductPremiumRate(keyFactorMap,
// product);
// logger.debug(" ^^^^ Fire premium rate : " + enPrmRate);
//
// }
// }
