package org.zerock.api01.util;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@Log4j2
public class JWTUtilTests {

  @Autowired
  private JWTUtil jwtUtil;

  @Test
  public void testGenerate() {
    Map<String, Object> claimMap = Map.of("mid", "ABCDE");

    String jwtStr = jwtUtil.generateToken(claimMap,1);

    log.info(jwtStr);
  }

  @Test
  public void testValidate() {

    //유효기간 지난 토큰

    String jwtStr = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTczMzg2NTUsIm1pZCI6IkFCQ0RFIiwiaWF0IjoxNzE3MzM4NTk1fQ.2gyR3YwtAizjPkVWRm-am2fmrL_YnMqWPvVTiYbu8v0";

    Map<String, Object> claim = jwtUtil.validateToken(jwtStr);

    log.info(claim);
  }

  @Test
  public void testAll() {

    String jwtStr = jwtUtil.generateToken(Map.of("mid","AAAA","email","aaaa@bbb.com"),1);

    log.info(jwtStr);

    Map<String, Object> claim = jwtUtil.validateToken(jwtStr);

    log.info("MID: " + claim.get("mid"));
    log.info("EMAIL: " + claim.get("email"));
  }
}
