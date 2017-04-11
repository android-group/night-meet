# night-meet api

## Приложение знакомств


### Описание API

<table>
  <tr>
    <td>Метод</td>
    <td>URL</td>
    <td>Действие</td>
    <td>Ответ</td>
    <td>Пример CURL запроса</td>
  </tr>
  <tr>
    <td>POST</td>
    <td>/api/v1/account/{id}</td>
    <td>вход пользователя с идентификатором {id}</td>
    <td>{"result":"ok"}</td>
    <td>curl -X POST http://localhost:8888/api/v1/account/{id}</td>
  </tr>
  <tr>
     <td>GET</td>
     <td>/api/v1/account/{id}/candidates?count=<количество элементов>&token=<токен авторизации в vk.com></td>
     <td>получение людей для просмотра. Если в списке людей нет, тогда получаю людей противоположного пола из моего города</td>
     <td>{"result":"ok","account_ids":["first_id","second_id"]}</td>
     <td>curl -X GET http://localhost:8888/api/v1/account/{id}/candidates?count=<количество элементов>&token=<токен авторизации в vk.com></td>
   </tr>
   <tr>
     <td>PUT</td>
     <td>/api/v1/account/{id}/relations/{other_id}/{type}</td>
     <td>изменение статуса отношений между пользователем с идентификатором {id} и {other_id} на статус {type}</td>
     <td>{"result":"ok"}</td>
     <td>curl -X PUT http://localhost:8888/api/v1/account/{id}/relations/{other_id}/{type}</td>
    </tr>
    <tr>
     <td>GET</td>
     <td>/api/v1/account/{id}/relations/{type}</td>
     <td>получение отношений со статусом {type} для пользователя с идентификатором {id}</td>
     <td>{"result":"ok","account_ids":["first_id","second_id"]}</td>
     <td>curl -X GET http://localhost:8888/api/v1/account/{id}/relations/{type}</td>
   </tr>
</table>


### Дополнения:
 
виды отношений:
   - like (1) - односторонняя симпатия
   - connect (2) - взаимная симпатия
   - viewed (3) -  анкета была просмотрена текущим пользователем


**в случае ошибки, ответ для всех методов будет следующий:**

 ```json
  {
       "result":"error",
       "description":"описание ошибки"
  }
 ```
 
 
## Установка приложения:
### Docker 

сборка 

> docker build -t night-meet .

запуск

> docker run -p <внешний порт для доступа к приложению>:8888 -v /<директория с базой монги>:/opt/mongo night-meet


   

