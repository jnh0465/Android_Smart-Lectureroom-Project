var MongoClient = require('mongodb').MongoClient;
var url = 'mongodb://localhost:27017';
var db;


MongoClient.connect(url, function (err, client) {
    if (err) {
       console.error('MongoDB 연결 실패', err);
       return;
    }
    db = client.db('SMART_LECTUREROOM');
 });

function buildQueryPack(docsPack){
    const queryObject = docsPack.queryObject;
    let docsObject=docsPack.docsObject;

    return new Promise((resolve, reject)=>{
        let _queryObject =queryObject;
        const queryPack = setQueryPack(_queryObject, docsObject);
        resolve(queryPack);
     });

}

 function setQueryPack(queryObject, docsObject){ //어떤 내용으로 쿼리할지 정보를 담은 객체를 set
     return {
        queryObject : queryObject, //이번에 쿼리할 내용
        docsObject : docsObject //이전 쿼리들의 결과물(이전에 쿼리한 이력들)
    };
 }

 function setDocsPack(docs, docsObject){ //쿼리를 한 결과물을 담을 객체
     return {
         docs : docs, // 쿼리 결과물
         docsObject : docsObject, //이전 쿼리들의 결과물(이전에 쿼리한 이력들)
         queryObject : "" //다음에 쿼리할 내용
    };
 }

 function queryStudentByID(queryPack){
     const queryObject = queryPack.queryObject;
     let docsObject = queryPack.docsObject;

     return new Promise((resolve, reject)=>{
        const student = db.collection('TB_STUDENT');
        student.find(queryObject).toArray(function(err, docs){
            console.log("------------------");
            console.log("queryStudentByID 쿼리한 내용");
            console.log(JSON.stringify(docs));
            console.log("사용한 쿼리문");
            console.log(JSON.stringify(queryObject));
            console.log("------------------");

            docsObject.queryStudentByID = docs;
            const docsPack = setDocsPack(docs, docsObject);
            resolve(docsPack);
        });
     });
 }

 function insertStudent(insertObject){
    return new Promise((resolve, reject)=>{
        const student = db.collection('TB_STUDENT');
        student.insertOne(insertObject, function(error, res){
            console.log("------------------");
            console.log("insertStudent 입력한 내용");
            console.log(JSON.stringify(insertObject));
            console.log("------------------");
            resolve();
        });
    });
 }

 function updateStudent(updateObject){
    return new Promise((resolve, reject)=>{
        const student = db.collection('TB_STUDENT');
        student.updateOne(updateObject.query, updateObject.update);
    });
 }

 module.exports = {
    buildQueryPack : buildQueryPack,
    setQueryPack : setQueryPack,
    setDocsPack : setDocsPack,
    queryStudentByID : queryStudentByID,
    insertStudent : insertStudent,
    updateStudent : updateStudent
}
