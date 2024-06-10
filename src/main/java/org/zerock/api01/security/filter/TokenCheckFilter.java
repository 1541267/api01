package org.zerock.api01.security.filter;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.api01.security.APIUserDetailsService;
import org.zerock.api01.security.exception.AccessTokenException;
import org.zerock.api01.util.JWTUtil;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {

  //872p JWT검증 끝난 이후 인증정보를 구성
  private final APIUserDetailsService apiUserDetailsService;
  private final JWTUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    String path = request.getRequestURI();

    if (!path.startsWith("/api/")) {
      filterChain.doFilter(request, response);
      return;
    }

    log.info("Token Check Filter.................. JWTUil: " + jwtUtil);

    //814p token에 문제가 있을 경우 자동으로 브라우저에 에러메세지 * 상태코드 함께 전송

//    try{
//      validateAccessToken(request);
//      filterChain.doFilter(request, response);
//
//    } catch(AccessTokenException accessTokenException) {
//      accessTokenException.sendResponseError(response);
//    }
//  }

  //873p jwt 검증 완료 후 스프링시큐리티를 위해 인증정보 구성
    try {
      Map<String, Object> payload = validateAccessToken(request);

      //mid
      String mid = (String)payload.get("mid");

      log.info("mid: " + mid);

      UserDetails userDetails = apiUserDetailsService.loadUserByUsername(mid);

      UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                      userDetails, null, userDetails.getAuthorities());

      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);
    } catch (AccessTokenException accessTokenException) {
      accessTokenException.sendResponseError(response);
    }

  }

  //813p 토큰 검증후 발생하는 에러 
  private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {
    String headerStr = request.getHeader("Authorization");

    if(headerStr == null || headerStr.length() < 8) {
      throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
    }

    //Bearer 생략
    String tokenType = headerStr.substring(0,6);
    String tokenStr = headerStr.substring(7);

//    if(tokenType.equalsIgnoreCase("Bearer") == false){
    if(!tokenType.equalsIgnoreCase("Bearer")){
      throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
  }
    try {
      Map<String, Object> values = jwtUtil.validateToken(tokenStr);

      return values;
    } catch (MalformedJwtException malformedJwtException) {

      log.error("MalformedJwtException-------------------------");
      throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);

    } catch (SignatureException signatureException){

      log.error("SignatureException----------------------------");
      throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);

    } catch (ExpiredJwtException expiredJwtException) {

      log.error("ExpiredJwtException---------------------------");
      throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
    }
  }
}
