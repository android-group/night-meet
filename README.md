# night-meet
API
добавление пользователя           (POST)   account?id=<текущий пользователь>
 
response:
{
  result:"ok"
}
----------------------------------------
 
поиск пользователя                (GET)    search?id=<текущий пользователь>&count=<количество>
 
response:
{
  ids:[id,id,id]
}
 
----------------------------------------
 
нажатие кнопки                   (POST)   submit?id=<текущий пользователь>&type=<int>
 
                            type: 1 - love
                                  2 - hate
                                 
response:
{
  result:"ok"
}
 
-----------------------------------------
 
получить симпатии                 (GET)     empathies?id=<текущий пользователь>&type=<int>
 
response:
{
 ids:[id,id,id]
}
                            type: 1 - is_show
                                  2 - is_now_show
   
-----------------------------------------
изменение статуса                 (PUT)     empathies?id=<текущий пользователь>&type=<int>
 
response:
{
   result:"ok"
}
