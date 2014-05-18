android-course
==============

Android application for android course
Bugs inside HeadHunter and Android API
1)BitmapFactory.decode methods have issues. ( Not all jpeg image can be decoded https://code.google.com/p/android/issues/detail?id=6066)
2)Some vacancies find from request `GET /vacancies/?text={text}`, but if with some id will get detailed vacancy, api show error `{"description": "Not Found"}`