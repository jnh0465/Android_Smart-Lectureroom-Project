var baseDBFunction = require('../baseDBFunction'); //mongoDB 관련 커스텀함수 모듈

 module.exports = {
    findStudent : (queryObject)=>{
        const queryPack = baseDBFunction.setQueryPack(queryObject,{});
        return baseDBFunction.queryStudent(queryPack);
    },
    insertStudent : (insertObject)=>{
        return baseDBFunction.insertStudent(insertObject);
    },
    updateStudent : (updateObject)=>{
        return baseDBFunction.updateStudent(updateObject);
    }
 }
