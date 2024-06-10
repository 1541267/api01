package org.zerock.api01.util;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
@Log4j2
public class JWTUtil {

  @Value("${org.zerock.jwt.secret}")
  private String key;

  public String generateToken(Map<String, Object> valueMap, int days) {
    log.info("JWTUtil GenerateKey..................." + key);


    //헤더
    Map<String, Object> headers = new HashMap<>();
    headers.put("typ", "JWT");
    headers.put("alg", "HS256");

    //payload
    Map<String, Object> payloads = new HashMap<>();
    payloads.putAll(valueMap);

    //테스트 시에는 짧은 유효 기간
    int time = (60 * 24) * days; // 테스트는 분단위로 나중 60*24(일) 단위변경
//    int time = (1) * days; // 테스트는 분단위로 나중 60*24(일) 단위변경
//    int time = 1; // 확인용

    String jwtStr = Jwts.builder()
            .setHeader(headers)
            .setClaims(payloads)
            .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
            .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))
            .signWith(SignatureAlgorithm.HS256, key.getBytes())
            .compact();

    return jwtStr;
  }

  public Map<String, Object> validateToken(String token) throws JwtException {

    Map<String, Object> claim = null;


    claim = Jwts.parser()
            .setSigningKey(key.getBytes()) //Set Key
            .parseClaimsJws(token) // 파싱 * 검증, 실패시 에러
            .getBody();

    return claim;
  }
}
