var express = require('express');
var router = express.Router();
/* Service module
*  process에 관한 리턴값은 {STATE:" " , DETAIL : " "}
*  형식을 유지함 */
var service = require('../../extend_modules/service/student_web/service');

const webName = "student_web";
const viewPath = "student_web/page";
const templatePath = "student_web/template";


/* 안드로이드 요청 */

//회원가입(백도어)
router.post('/process/registerProcess', function(req, res, next) {
  const userInfo = req.body;
  service.registerProcess(userInfo, (result)=>{
    const STATE = result.STATE;
    const DETAIL = result.DETAIL;
    if(STATE === "SUCCESS"){
      res.json(1);
    }
    if(STATE ==="ERR"){
      if(DETAIL === "ALEADY_REGISTERED_ID"){
        res.json("이미 존재하는 아이디입니다.");
      }
    }
  });
});

//비밀번호변경
router.post('/process/changePasswordProcess', function(req, res, next) {
  const userInfo = req.body;
  service.changePasswordProcess(userInfo, (result)=>{
    const STATE = result.STATE;
    const DETAIL = result.DETAIL;
    if(STATE ==="SUCCESS"){
      res.json(1); //로그인 성공

      req.session.user = { //로그인 세션 생성
        id : result.data.id,
        name : result.data.name
      }
      console.log("세션생성 : " + req.session.user.id);
    }
    if(STATE ==="ERR") {
      if(DETAIL ==="NOT_FOUND_ID"){
        res.json(2);
      }
    }
  });
});

//토큰업데이트
router.post('/process/getTokenProcess', function(req, res, next) {
  const userInfo = req.body;
  service.getTokenProcess(userInfo, (result)=>{
    console.log("json :  "+JSON.stringify(userInfo));//
    const STATE = result.STATE;
    const DETAIL = result.DETAIL;
    if(STATE ==="SUCCESS"){
      res.json(1); //로그인 성공

      req.session.user = { //로그인 세션 생성
        id : result.data.id,
        name : result.data.name
      }
      console.log("세션생성 : " + req.session.user.id);
    }
    if(STATE ==="ERR") {
      if(DETAIL ==="NOT_FOUND_ID"){
        res.json(2);
      }
    }
  });
});

//로그인
router.post('/process/loginProcess', function(req, res, next) {
  const userInfo = req.body;
  service.loginProcess(userInfo, (result)=>{
    const STATE = result.STATE;
    const DETAIL = result.DETAIL;
    if(STATE ==="SUCCESS"){
      req.session.user = { //로그인 세션 생성
        id : result.data.id,
        name : result.data.name
      }
      console.log("세션생성 : " + req.session.user.id);
      res.json(1); //로그인 성공
    }
    if(STATE ==="ERR") {
      if(DETAIL ==="EMPTY_ID"){
        res.json(0);
      }
      if(DETAIL ==="EMPTY_PASSWORD"){
        res.json(0);
      }
      if(DETAIL ==="NOT_CORRECT_PASSWORD"){
        res.json(0);
      }
      if(DETAIL ==="NOT_FOUND_ID"){
        res.json(2);
      }

    }
  });
});

//로그
router.post('/process/getScheduleProcess', function(req, res, next) {
  const userInfo = req.body;

  service.getScheduleProcess(userInfo, (result)=>{
    const STATE = result.STATE;
    const DETAIL = result.DETAIL;

    console.log ("쿼리내용 :"+ JSON.stringify (DETAIL)); //끌려왔습니다아아아ㅏ
    if(STATE ==="SUCCESS"){
      res.json(JSON.stringify (DETAIL));
    }
  });
});


//출석상태
router.post('/process/getAttendStateProcess', function(req, res, next) {
  const userInfo = req.body;

  service.getAttendStateProcess(userInfo, (result)=>{
    const STATE = result.STATE;
    const DETAIL = result.DETAIL;

    console.log ("출석상태쿼리내용 :"+ JSON.stringify (DETAIL)); //끌려왔습니다아아아ㅏ
    if(STATE ==="SUCCESS"){
      res.json(JSON.stringify (DETAIL));
    }
  });
});

module.exports = router;
