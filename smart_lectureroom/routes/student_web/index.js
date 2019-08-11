var express = require('express');
var router = express.Router();
/* Service module
*  process에 관한 리턴값은 {STATE:" " , DETAIL : " "}
*  형식을 유지함 */
var service = require('../../extend_modules/service/student_web/service');

const webName = "student_web";
const viewPath = "student_web/page";
const templatePath = "student_web/template";

var multer, storage, path, crypto;
multer = require('multer')
path = require('path');
crypto = require('crypto');

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
    const STUDENTNAME = result.STUDENTNAME; //////////

    if(STATE ==="SUCCESS"){
      req.session.user = { //로그인 세션 생성
        id : result.data.id,
        name : result.data.name
      }
      console.log("세션생성 : " + req.session.user.id);
      res.json(STUDENTNAME); //로그인 성공
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

//시간표
router.post('/process/getScheduleProcess', function(req, res, next) {
  const userInfo = req.body;

  service.getScheduleProcess(userInfo, (result)=>{
    const STATE = result.STATE;
    const DETAIL = result.DETAIL;

    // console.log ("시간표쿼리내용 :"+ JSON.stringify (DETAIL)); //끌려왔습니다아아아ㅏ
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

// 학생사진 업로드
 var testid;
  var storage = multer.diskStorage({
    destination: './resources/images/student/',
    filename: function(req, file, cb) {
        return cb(null, testid+".jpg");
  }});

  router.post("/process/upload",
    multer({
      storage: storage
    }).single('upload'), function(req, res) {
      const userInfo = req.body;
        testid = userInfo.student_id;
      return res.status(200).end();
  });


// 고려해야할 푸시 타입 3가지
// 1. late , attendStartTime 이 없는 푸시 ----> 애초에 푸시에 버튼이 없음
// 2. 출석재요청을 위한 푸시요청 ------> 이의신청기한이 지나기전에 온것만 결과를 반영
// 3. 출석요청을 위한 푸시요청 ------> late값과 비교하여 지각 또는 결석으로 반영

router.post("/process/pushResponseProcess" , function(req, res, next){
  const attendInfo = JSON.parse(req.body.student_attendstate);
  // res.send({STATE : "SUCCESS" , DETAIL : "TEST"});
  console.log ("푸시요청: "+JSON.stringify(attendInfo));
  service.pushResponseProcess(attendInfo, (result)=>{
    // res.send(result);
    res.json(1);
  });
});

module.exports = router;
