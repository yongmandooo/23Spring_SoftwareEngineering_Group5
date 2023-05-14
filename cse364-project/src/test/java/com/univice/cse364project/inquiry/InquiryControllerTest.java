package com.univice.cse364project.inquiry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.opencsv.exceptions.CsvException;
import com.univice.cse364project.device.DeviceRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.univice.cse364project.user.User;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InquiryControllerTest {
    @Autowired
    private InquiryController inquiryController;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private InquiryRepository inquiryRepository;
    @BeforeAll
    void beforeTest() throws IOException, CsvException {
        //given
        ObjectMapper om = new ObjectMapper();

        ObjectNode inquiry = om.createObjectNode();
        inquiry.put("id", "inquirytest");
        inquiry.put("title", "testtitle");
        inquiry.put("contents", " testboard");
        inquiry.put("confirmed", false);
        ObjectNode requestBody = om.createObjectNode();
        Query query = new Query();
        query.addCriteria(Criteria.where("isAdmin").is(false));
        User u = mongoTemplate.findOne(query, User.class);
        String authid = u.getAuthenticationId();
        requestBody.put("inquiry",inquiry);
        requestBody.put("authenticationId",authid);

        //when
        inquiryController.insertBoard(requestBody);

        ObjectNode inquiry2 = om.createObjectNode();
        inquiry2.put("id", "inquirytest");
        inquiry2.put("title", "testtitle");
        inquiry2.put("contents", " testboard");
        inquiry2.put("confirmed", false);
        ObjectNode requestBody2 = om.createObjectNode();
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("studentId").is("20201234"));
        User u2 = mongoTemplate.findOne(query2, User.class);
        String authid2 = u2.getAuthenticationId();
        requestBody2.put("inquiry",inquiry2);
        requestBody2.put("authenticationId",authid2);

        //when
        inquiryController.insertBoard(requestBody2);

    }
    @Test
    @DisplayName("Write new board with sufficient ")
    void writeboardwithInSufficientId() {
        //given
        ObjectMapper om = new ObjectMapper();
        ObjectNode inquiry = om.createObjectNode();
        inquiry.put("id", "inquiry7");
        inquiry.put("title", "chantest1!");
        inquiry.put("contents", "invalide board");
        inquiry.put("confirmed", false);
        ObjectNode requestBody = om.createObjectNode();
        requestBody.put("inquiry", inquiry);
        requestBody.put("authenticationId","111");
        //when
        //then
        assertThrows(WrongAuthenticationIdException.class, ()->inquiryController.insertBoard(requestBody));
    }


    @Test
    @DisplayName("Write new board")
    void insertBoard() throws JsonProcessingException {
        //given
        ObjectMapper om = new ObjectMapper();

        ObjectNode inquiry = om.createObjectNode();
        inquiry.put("id", "inquiry8");
        inquiry.put("title", "chantest8!");
        inquiry.put("contents", " board");
        inquiry.put("confirmed", false);
        ObjectNode requestBody = om.createObjectNode();
        Query query = new Query();
        query.addCriteria(Criteria.where("isAdmin").is(false));
        User u = mongoTemplate.findOne(query, User.class);
        String authid = u.getAuthenticationId();



        requestBody.put("inquiry",inquiry);

        requestBody.put("authenticationId",authid);

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("id").is("inquiry8"));

        //when
        inquiryController.insertBoard(requestBody);
        //then
        Inquiry newboard = mongoTemplate.findOne(query1, Inquiry.class);
        assertTrue(newboard != null && newboard instanceof Inquiry);

    }


    @Test
    @DisplayName("Put board with not login")
    void PutboardwithNotlogin() {
        //given
        ObjectMapper om = new ObjectMapper();

        ObjectNode inquiry = om.createObjectNode();
        inquiry.put("id", "inquiry5");
        inquiry.put("title", "chantest5!");
        inquiry.put("contents", "can make board");
        inquiry.put("confirmed", false);
        inquiry.put("writer", "22222222222");
        ObjectNode requestBody = om.createObjectNode();
        requestBody.put("inquiry", inquiry);
        requestBody.put("authenticationId","1111111111111");


        //when
        //then
        assertThrows(WrongAuthenticationIdException.class, ()->inquiryController.editboard(requestBody,"inquiry8"));
    }


    @Test
    @DisplayName("Put board with Insufficientauthority")
    void PutboardwithInsufficientauthority() {
        //given
        ObjectMapper om = new ObjectMapper();
        ObjectNode inquiry = om.createObjectNode();
        ObjectNode requestBody = om.createObjectNode();
        Query query = new Query();
        query.addCriteria(Criteria.where("isAdmin").is(false));
        User u = mongoTemplate.findOne(query, User.class);
        String authid = u.getAuthenticationId();
        inquiry.put("id", "inquiry5");
        inquiry.put("title", "chantest5!");
        inquiry.put("contents", "can make board");
        inquiry.put("confirmed", false);
        requestBody.put("inquiry", inquiry);
        requestBody.put("authenticationId",authid);

        //when
        //then
        assertThrows(InsufficientpermissionException.class, ()->inquiryController.editboard(requestBody,"inquiry4"));
    }

    @Test
    @DisplayName("Udmin Put board with Insufficientauthority")
    void PutadminboardwithInsufficientauthority() throws JsonProcessingException{
        //given
        ObjectMapper om = new ObjectMapper();
        ObjectNode inquiry = om.createObjectNode();
        ObjectNode requestBody = om.createObjectNode();
        Query query = new Query();
        query.addCriteria(Criteria.where("isAdmin").is(true));
        User u = mongoTemplate.findOne(query, User.class);
        String authid = u.getAuthenticationId();
        inquiry.put("id", "inquiry77");
        inquiry.put("title", "chantest5!");
        inquiry.put("contents", "can make board");
        inquiry.put("confirmed", false);
        requestBody.put("inquiry", inquiry);
        requestBody.put("authenticationId",authid);
        Query query1 = new Query();

        //when
        inquiryController.editboard(requestBody, "inquiry3");
        //then
        query1.addCriteria(Criteria.where("id").is("inquirytest"));
        Inquiry newboard = mongoTemplate.findOne(query1, Inquiry.class);

        assertTrue(newboard!=null);
    }

    @Test
    @DisplayName("Edit board")
    void editboard() throws JsonProcessingException {
        //given
        ObjectMapper om = new ObjectMapper();
        Query query = new Query();
        query.addCriteria(Criteria.where("isAdmin").is(true));
        User u = mongoTemplate.findOne(query, User.class);
        String authid = u.getAuthenticationId();
        ObjectNode inquiry = om.createObjectNode();

        inquiry.put("id", "inquirytest");
        inquiry.put("title", "chantestischange");
        inquiry.put("contents", " boardischanged");
        inquiry.put("confirmed", false);
        ObjectNode requestBody = om.createObjectNode();
        requestBody.put("inquiry",inquiry);
        requestBody.put("authenticationId",authid);
        String beforecontents = "testboard";
        String beforetitle = "testtitle";
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("id").is("inquirytest"));

        //when

        inquiryController.editboard(requestBody, "inquirytest");
        //then
        Inquiry newboard = mongoTemplate.findOne(query1, Inquiry.class);
        assertTrue(!beforecontents.equals(newboard.getContents()) || !beforetitle.equals(newboard.getTitle()));
    }
    @Test
    @DisplayName("Edit board2")
    void editboard2() throws JsonProcessingException {
        //given
        ObjectMapper om = new ObjectMapper();
        Query query = new Query();
        query.addCriteria(Criteria.where("isAdmin").is(true));
        User u = mongoTemplate.findOne(query, User.class);
        String authid = u.getAuthenticationId();
        ObjectNode inquiry = om.createObjectNode();


        inquiry.put("id", "inquirytest3");
        inquiry.put("title", "chantestischange3");
        inquiry.put("contents", " boardischanged3");
        inquiry.put("confirmed", false);
        ObjectNode requestBody = om.createObjectNode();
        requestBody.put("inquiry",inquiry);
        requestBody.put("authenticationId",authid);
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("id").is("inquirytest2"));

        //when

        inquiryController.editboard(requestBody, "inquirytest2");
        //then
        Inquiry newboard = mongoTemplate.findOne(query1, Inquiry.class);
        assertTrue(newboard!=null);
    }
    @Test
    @DisplayName("Put board with not login")
    void PutboardsolvedwithNotlogin() {
        //given
        ObjectMapper om = new ObjectMapper();
        ObjectNode requestBody = om.createObjectNode();
        requestBody.put("authenticationId","1111111111111");


        //when
        //then
        assertThrows(WrongAuthenticationIdException.class, ()->inquiryController.Solved(requestBody,"inquiry8"));
    }

    @Test
    @DisplayName("Put board with invalid")
    void PutboardsolvedwithInsufficientauthority() {
        //given
        ObjectMapper om = new ObjectMapper();
        ObjectNode requestBody = om.createObjectNode();
        Query query = new Query();
        query.addCriteria(Criteria.where("isAdmin").is(false));
        User u = mongoTemplate.findOne(query, User.class);
        String authid = u.getAuthenticationId();
        requestBody.put("authenticationId",authid);

        //when
        //then
        assertThrows(InsufficientpermissionException.class, ()->inquiryController.Solved(requestBody,"inquiry8"));
    }

    @Test
    @DisplayName("Put board with invalid")
    void PutboardsolvedwithThereisnoboard() {
        //given
        ObjectMapper om = new ObjectMapper();
        ObjectNode requestBody = om.createObjectNode();

        Query query = new Query();
        query.addCriteria(Criteria.where("isAdmin").is(true));
        User u = mongoTemplate.findOne(query, User.class);
        String authid = u.getAuthenticationId();
        requestBody.put("authenticationId", authid);

        //when
        //then
        assertThrows(InvalidInquiryException.class, ()->inquiryController.Solved(requestBody,"inquiryno"));
    }

    @Test
    @DisplayName("Board is confirmed")
    void Solved() throws JsonProcessingException {
        //given
        ObjectMapper om = new ObjectMapper();
        ObjectNode requestBody = om.createObjectNode();

        Query query = new Query();
        query.addCriteria(Criteria.where("isAdmin").is(true));
        User u = mongoTemplate.findOne(query, User.class);
        String authid = u.getAuthenticationId();


        Query query1 = new Query();
        query1.addCriteria(Criteria.where("id").is("inquiry3"));
        boolean before = false;

        requestBody.put("authenticationId",authid);
        //when
        inquiryController.Solved(requestBody, "inquiry3");
        //then
        Inquiry newboard = mongoTemplate.findOne(query1, Inquiry.class);
        assertTrue(before != newboard.isConfirmed());

    }


    @Test
    @DisplayName("Delete board with not login")
    void DeleteboardwithNotlogin() {
        //given
        ObjectMapper om = new ObjectMapper();
        ObjectNode requestBody = om.createObjectNode();
        requestBody.put("authenticationId","1111111111111");

        //when
        //then
        assertThrows(WrongAuthenticationIdException.class, ()->inquiryController.deleteBoard(requestBody, "inquirytest"));
    }

    @Test
    @DisplayName("Delete board with invalid")
    void DeleteboardwithInsufficientauthority() {
        //given
        ObjectMapper om = new ObjectMapper();
        ObjectNode requestBody = om.createObjectNode();

        Query query = new Query();
        query.addCriteria(Criteria.where("isAdmin").is(false));
        User u = mongoTemplate.findOne(query, User.class);
        String authid = u.getAuthenticationId();
        requestBody.put("authenticationId",authid);
        //when
        //then
        assertThrows(InsufficientpermissionException.class, ()->inquiryController.deleteBoard(requestBody, "inquiry1"));
    }


    @Test
    @DisplayName("Delete Board2")
    void deleteBoard() throws JsonProcessingException {
        //given

        ObjectMapper om = new ObjectMapper();
        ObjectNode requestBody = om.createObjectNode();

        Query query = new Query();
        query.addCriteria(Criteria.where("isAdmin").is(true));
        User u = mongoTemplate.findOne(query, User.class);
        String authid = u.getAuthenticationId();

        requestBody.put("authenticationId",authid);

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("writer").is(authid));

        //when
        inquiryController.deleteBoard(requestBody, "inquirytest");
        //then
        Inquiry deleted = mongoTemplate.findOne(query1, Inquiry.class);
        assertTrue(deleted == null);

    }

    @Test
    @DisplayName("Delete Board")
    void deleteBoard2() throws JsonProcessingException {
        //given


        ObjectMapper om = new ObjectMapper();
/*
        ObjectNode inquiry = om.createObjectNode();
        inquiry.put("id", "inquiry18");
        inquiry.put("title", "chantest18!");
        inquiry.put("contents", " board18");
        inquiry.put("confirmed", false);
        ObjectNode requestBody = om.createObjectNode();
        Query query = new Query();
        query.addCriteria(Criteria.where("isAdmin").is(false));
        User u = mongoTemplate.findOne(query, User.class);
        String authid = u.getAuthenticationId();

        requestBody.put("inquiry",inquiry);
        requestBody.put("authenticationId",authid);

        inquiryController.insertBoard(requestBody);
*/
        ObjectNode requestBody1 = om.createObjectNode();

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("isAdmin").is(true));
        User u1 = mongoTemplate.findOne(query1, User.class);
        String authid1 = u1.getAuthenticationId();

        requestBody1.put("authenticationId",authid1);

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("writer").is(authid1));

        //when
        inquiryController.deleteBoard(requestBody1, "inquiry5");
        //then
        Inquiry deleted = mongoTemplate.findOne(query2, Inquiry.class);
        assertTrue(deleted == null);

    }
    @AfterAll
    void afterAll() throws IOException, CsvException{
        Query query = new Query();
        query.addCriteria(Criteria.where("title").is("chantest8!"));
        Inquiry test = mongoTemplate.findOne(query, Inquiry.class);
        mongoTemplate.remove(test);

        Query query2 = new Query();
        query2.addCriteria(Criteria.where("title").is("chantestischange3"));
        Inquiry test2 = mongoTemplate.findOne(query2, Inquiry.class);
        mongoTemplate.remove(test2);

        ObjectMapper om = new ObjectMapper();
        ObjectNode inquiry = om.createObjectNode();
        inquiry.put("id", "inquiry5");
        inquiry.put("title", "test5");
        inquiry.put("contents", " content5");
        inquiry.put("confirmed", false);
        ObjectNode requestBody = om.createObjectNode();
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("isAdmin").is(false));
        User u = mongoTemplate.findOne(query1, User.class);
        String authid = u.getAuthenticationId();
        requestBody.put("inquiry",inquiry);
        requestBody.put("authenticationId",authid);

        inquiryController.insertBoard(requestBody);

    }

}