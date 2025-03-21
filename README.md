DEVELOPING A JAVA APPLICATION FOR LIBRARY MANAGEMENT
---

AUTHOR
---
Group 8 – N1
1.	Nguyễn Thu Hà – 23020532
2.	Lê Minh Anh – 23020510
3.	Lưu Minh Anh – 23020511

DESCRIPTION
---
The application is designed to support library management. The application is written in Java and uses the JavaFX library. The application follows the MVC model. The application supports role-based access for two types of users: librarians and students. This application includes features for managing books, such as categorizing books, managing book information, tracking their availability, and managing transactions with the user information table. It also monitors the working hours of librarians. The application uses the book, user, transaction, and attendance tables to store data in the database.
1.	The application is designed to support library management. 
2.	The application is written in Java and uses the JavaFX library.
3.	The application follows the MVC model.
4.	The application supports role-based access for two types of users: librarians and students. 
5.	This application includes features for managing books, such as categorizing books, managing book information, tracking their availability, and managing transactions with the user information table. It also monitors the working hours of librarians. 
6.	The application uses the book, user, transaction, and attendance tables to store data in the database.

UML DIAGRAM
---
![462570282_919336326462773_8470600518205289536_n](https://github.com/user-attachments/assets/a8fa3e3b-45c6-4670-b69b-b939d95168ac) 
![467475421_1539008283421557_740871534532002519_n](https://github.com/user-attachments/assets/0996f80c-001b-4f12-91bf-cb8c0413782e)



INSTALATION
---
1.	Clone the project from the repository.
2.	Open the project in the IDE: If you don't have sql yet, download them and change the information in the database connection section to your account.
3.	Run the project.


USAGE
---
1.	Login: 
-	Student: 
+ Fill in username and password and click the sign-in button if already have an account.
+ Click the Sign-up button and fill in the required information to register an account.
-	Librarian: only need to fill in username and password and click the sign-in button.
2.	Interface for Librarian:
-	To control Books, click Book button on menu bar:
+ To add a book, click the Add Book button.
    ~ Click the numbers to check ISBN, if it exists -> fill the quantity. If it does not exist in the library, fill all the fields and click Add Book to add that book.
    ~ Some ISBN EXAMPLES that you can check when adding a book : 9780451524935, 9780743273565, 9780316769488, 9780141040349, 9780451526342, 9780547928227, 9780399501487, 9780439023528, 9780525478812, 9780062024039, 9780385737944, 9780375842207, 9780786838653, 9780064471046, 9780147514011, 9780321349606, 9781449358459, 9781421501898, 9781974707557
+ To search for a book, text on the Searching.
+ To update a book, select in the select column one book and click the Update button.
+ To delete books, select in the select column and click the Delete button.
-	To review all transactions, click the Transactions button on menu bar and then in the transaction window click Add Transaction button to create a new transact.
-	To play game to earn extra coins for watering plants to get an extra day to pay for books.
-	To review all information about students, click Student button on the menu bar.
-	To change information about an account, click the Account button.
+ To change the password, click Change Pass button and Save button to save the change.
+ To edit account, click Edit button.
+ To delete account, click Delete Account button.
+ For attendance to start working, click Check – in button.
+ To view the shifts of the librarians, click Attendance button.
+ At the end of the work shift, click Sign - out to check out.
3.	Interface for students:
-	To review all books in the library, click Book button on menu bar:
+ To read information a book, click the Book Card.
-	To review all student’s transactions, click the Transactions button on menu bar
+ To return the book, give the librarian a QR code of that book. 
-	To play game to earn extra coins for watering plants to get an extra day to pay for books:
+ To play game, click Game button.
+ To water tree, click Plant button.
-	To change information about an account, click the Account button.
+ To change the password, click Change Pass button and Save button to save the change.
+ To edit account, click Edit button.
+ To delete account, click Delete Account button.
+ For attendance to start working, click Check – in button.
+ To view the shifts of the librarians, click Attendance button.
+ At the end of the work shift, click Sign - out to check out.

GAME RULES
---
-	Coin Usage:
+ You can use 50 coins to extend the borrowing period of a book by 1 day.
+ However, each book can only be extended for a maximum of 7 days in total.
-	Plant Tree:
+ Check in daily to receive 1 water drop.
+ 10 water drops are required for the tree to grow one level (3 levels)
+ When the tree matures, you can harvest it to receive a 20-coin reward.
-	GAME 2048:
+ Earn water drops based on your score milestones:
~ At 2000 points, receive 1 water drop.
~ At 4000 points, receive 2 water drops.
~ At 6000 points, receive 3 water drops.
~ At 8000 points, receive 4 water drops.
~ At 10,000 points, receive 5 water drops.
+ Once a milestone is reached (e.g., 2000 scores), the daily progress bar will start tracking scores for the next milestone.
+ A milestone can only be claimed once per day, and you cannot earn rewards for the same score milestone again (e.g., reaching 2000 scores).
+ The daily progress bar resets at the start of a new day.

DEMO
---
FOR LIBRARIAN
![462576061_1264178461493495_911083020970611448_n(1)(1)](https://github.com/user-attachments/assets/44e4828e-b928-4b53-89c0-43d086ebc0d3) -- home-lib
![462645600_881217390839136_4859138977335090738_n(1)(1)](https://github.com/user-attachments/assets/871201f3-d824-40c8-964e-96fa36779144) -- stu-lib
![467474861_587421616962706_1324729333485888987_n(1)(1)](https://github.com/user-attachments/assets/a7615ad4-e764-4b2a-88b5-c1ea6372e72c) -- trans-lib
![462557519_1493011751384559_8634863428489692321_n(1)(1)](https://github.com/user-attachments/assets/bd305130-c777-4c09-968a-8ffe349f6a8e) -- books-lib
![466706100_1985368538646536_7854839384994613597_n(1)(1)](https://github.com/user-attachments/assets/c51617af-df8c-4adb-a6a9-9d83263d3257) -- add book
![467481694_548802588045113_6377918823323689623_n(1)(1)](https://github.com/user-attachments/assets/e09f4349-1e6f-4478-8895-6e622cac444e) -- update book
![462640534_2038372053242936_3055273118486370678_n(1)(1)](https://github.com/user-attachments/assets/37b021dc-3346-4346-b318-1f4fa77ae797) -- attendance

FOR STUDENTS
![462586242_564811672806754_8690334401035911310_n(1)(1)](https://github.com/user-attachments/assets/c8cbe737-58b3-4bba-81e3-02057073728f) -- home-stu
![462644429_551176114376584_3234039375118667591_n(1)(1)](https://github.com/user-attachments/assets/ff4e8f83-406e-4a04-b140-e210db9b8a14) -- trans-stu
![462645362_1297275704641258_3956486807944027769_n(1)(1)](https://github.com/user-attachments/assets/defea793-b48f-4100-8988-892ef06ab8bf) -- books-stu
![462566008_1624642128117886_888154744852732320_n(1)(1)](https://github.com/user-attachments/assets/c8756248-da43-4cc8-9846-2bebdffca102) -- game
![467476876_1168572181914902_5139384098141322771_n(1)(1)](https://github.com/user-attachments/assets/50af1c4b-e4d2-40e6-bff5-58f1e1e3bd0d) -- game

FOR ALL
![462566760_820106313473758_8748940390198923971_n(1)(1)](https://github.com/user-attachments/assets/9f71da46-c2c1-4d8a-97bb-3642ca5f17b1) -- sign-in
![467476879_1082177406514760_8405503000478967091_n(1)(1)](https://github.com/user-attachments/assets/ebddb157-4d95-435b-b05e-607d380d206b) -- book-details
![462583250_545931124938631_4129867590326880598_n(1)(1)](https://github.com/user-attachments/assets/607cf24a-4a4f-4227-b89a-b66aacd50df0) -- account
![462573127_588781540218488_8807570958884179353_n(1)(1)](https://github.com/user-attachments/assets/05e2d96b-baec-48bf-ab90-a81cdabb9b77) -- change pass

FUTURE IMPROVEMENTS
---
1.	Add more books.
2.	Add more complex games.
3.	Improve the user interface.

Project Status
---
The project is completed.

NOTES
---
The application is written for educational purposes.

SOURCES
---
https://github.com/nayuki/QR-Code-generator - QR
https://github.com/Code-With-BK/Java/blob/main/GameOf2048ConsoleApp.java - 2048
And thanks to GPT and Copilot for helping us.
