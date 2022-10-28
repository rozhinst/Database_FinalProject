drop trigger send_notif ;
delimiter //
create trigger send_notif after insert on BorrowHistory
FOR EACH ROW
BEGIN
if (New.result = 'successfull') then 
	set @message := Concat('User ',cast(NEW.UserId as char),' borrowed book ',cast(New.BorrowID as char),' on ', Cast(now()as char)) ;
	insert into LibraryMessage(Message)
    values(@message);
end if;
END //
delimiter ;        
-- ----------------------------------------------------------------------------------------------------------------------------------
drop trigger send_book_back_notif;
delimiter //
create trigger send_book_back_notif after update on Borrow
FOR EACH ROW
BEGIN
if !(New.ReturnDate <=> Old.ReturnDate) then 
	if now() > New.ReturnDate then
		set @message := Concat('Book ',cast(New.BookId as char),' returned ',cast(datediff(now(),New.ReturnDate) as char),'  days later than due date.') ;
		insert into LibraryMessage(Message)
		values(@message);
	else	
		insert into LibraryMessage(Message)
		values('Book '+New.BookId+' retruned on time');
    end if;
end if;
END //
delimiter ;        
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE sign_up(in user_name varchar(100),in first_name varchar(100),in last_name varchar(100),in addr varchar(512),
in phone char(11),in pass varchar(16),in acc_status varchar(10),
out success varchar(100),out passCombination varchar(100),out passCount varchar(100),out usernameCount varchar(100),out usernameUnique varchar(100),out usernameCombination varchar(100))
BEGIN
    declare rollbacked boolean default false;
    declare user_id int;
	set passCount = ''; 
    set usernameUnique ='';
    set usernameCount = '';
    set passCombination  = '';
    set usernameCombination  = '';
    set success  = 'Not Successful';


	 start transaction;
	 insert into customer(FirstName,LastName,Address,Phone)
	 values(first_name,last_name,addr,phone);
     if (select count(*) from account where Username = user_name) > 0 -- case sensivity asnd password
		then
			set usernameUnique = 'username is not unique!';
            set rollbacked = true;
            Rollback;
	else if (char_length(user_name) < 6 ) then
		set usernameCount = 'username length is less than 6';
        if rollbacked = false then
			set rollbacked = true;
			Rollback;
		end if;
	else if !(user_name regexp '([A-Za-z0-9_]+)') then
		set usernameCombination = 'username is weak';
        if rollbacked = false then
			set rollbacked = true;
			Rollback;
		end if;
	else if (char_length(pass) < 8) then
		set passCount = 'password length is less than 8';
			if rollbacked = false then
				set rollbacked = true;
				Rollback;
			end if;
	else if !(pass regexp '([A-Za-z0-9_]+)') then
		set passCombination = 'password is weak';
			if rollbacked = false then
				set rollbacked = true;
				Rollback;
			end if;
	else
		commit;
        set success = 'successful';
        set user_id = LAST_INSERT_ID();
		insert into account(UserId,Balance,CreationDate,UserPassword,Username,accountSatus)
		values(user_id,0,NOW(),md5(pass), user_name,acc_status);
        
	end if;
    end if;
    end if;
    end if;
    end if;
    select success,passCombination,passCount,usernameCount,usernameUnique, usernameCombination;
END//
delimiter ;
select * from account;
-- -----------------------------------------------------------------------------------------------------------------------------------------------
call sign_up('adbhihiii','admin','jojojo', 'elm street', '09291234567','studentt','librarian');
select * from account;
 
delimiter //
CREATE PROCEDURE sign_in(in user_name varchar(10),in pass varchar(16),out success varchar(100),out id int )
BEGIN
-- declare user_id int default -1;
-- declare psswd varchar(100);
set success = 'login not successfull';
-- set psswd = CAST(PASSWORD(pass) as char);
if (select count(*) from account where Username = user_name) > 0 then
	if (select UserId from account where md5(pass) = UserPassword and user_name = Username) > -1 then  -- case sensitivity
		set success = 'logged in successfully';
		select @id :=UserId from account where md5(pass) = UserPassword and user_name = Username;
		set id = @id;
		select success, id;
	else
		set id = -1;
		select success, id;
	end if;
end if;
END//
delimiter ;
select * from account;
-- call sign_in('rozhrozh','student123')
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE get_my_details(tag int)
BEGIN
select * from account natural join (select Id as UserId,FirstName,LastName,Phone,Address from customer where Id = tag) as result;
-- select * from account where tag = UserId;
END //
delimiter ;
select * from account;
select * from customer;
call get_my_details(8);
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_name(book_name varchar(100))
BEGIN

if book_name is  null then
	select 'field can not be empty';
else
	select * from book where Title = book_name order by Title;
end if;
END //
delimiter ;
select * from book;
call search_by_name('title');
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_edition(selected_edition int)
BEGIN

if selected_edition is null then
	select 'field can not be empty';
else
	select * from book where Edition = selected_edition order by Title;
end if;
END //
delimiter ;
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_published_date(published_date Date)
BEGIN

if published_date is null then
	select 'field can not be empty';
else
	select * from book where PublishedDate = published_date order by Title;
end if;
END //
delimiter ;

delimiter //
CREATE PROCEDURE search_by_publisher(publisher varchar(512))
BEGIN
if publisher is null then
	select 'field can not be empty';
else
	select * from book where PublisherName = publisher order by Title;
end if;
END //
delimiter ;
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_publisher_title(book_title varchar(512),publisher varchar(512))
BEGIN
if publisher is null or book_title is null then
	select 'fields can not be empty';
else
	select * from book where PublisherName = publisher and Title = book_title order by Title;
end if;
END //
delimiter ;

delimiter //
CREATE PROCEDURE search_by_publisher_edition(selected_edition int,publisher varchar(512))
BEGIN
if publisher is null or selected_edition is null then
	select 'fields can not be empty';
else
	select * from book where PublisherName = publisher and Edition = selected_edition order by Title;
end if;
END //
delimiter ;
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_publisher_date(published_date Date,publisher varchar(512))
BEGIN
if publisher is null or published_date is null then
	select 'fields can not be empty';
else
	select * from book where PublisherName = publisher and PublishedDate = published_date order by Title;
end if;
END //
delimiter ;
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_title_date(book_title varchar(512),published_date Date)
BEGIN
if published_date is null or book_title is null then
	select 'fields can not be empty';
else
	select * from book where PublishedDate = published_date and Title = book_title order by Title;
end if;
END //
delimiter ;
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_title_edition(book_title varchar(512),selected_edition int)
BEGIN
if selected_edition is null or book_title is null then
	select 'fields can not be empty';
else
	select * from book where Edition = selected_edition and Title = book_title order by Title;
end if;
END //
delimiter ;
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_edition_date(published_date Date,selected_edition int)
BEGIN
if published_date is null or selected_edition is null then
	select 'fields can not be empty';
else
	select * from book where Edition = selected_edition and PublishedDate = published_date order by Title;
end if;
END //
delimiter ;
-- ----------------------------------------------------------------------------------------------------------------------------------
select * from stores;
select * from book;
delimiter //
CREATE PROCEDURE search_by_title_edition_date(book_title varchar(512),selected_edition int, published_date Date)
BEGIN
if selected_edition is null or book_title is null or published_date is null then
	select 'fields can not be empty';
else
	select * from book where Edition = selected_edition and Title = book_title and PublishedDate = published_date order by Title;
end if;
END //
delimiter ;
call search_by_title_edition_date('title',1,'2021-02-03');
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_title_edition_publisher(book_title varchar(512),selected_edition int, publisher varchar(512))
BEGIN
if selected_edition is null or book_title is null or publisher then
	select 'fields can not be empty';
else
	select * from book where Edition = selected_edition and Title = book_title and PublisherName = publisher order by Title;
end if;
END //
delimiter ;
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_title_date_publisher(book_title varchar(512),published_date Date, publisher varchar(512))
BEGIN
if published_date is null or book_title is null or publisher then
	select 'fields can not be empty';
else
	select * from book where PublishedDate = published_date and Title = book_title and PublisherName = publisher order by Title;
end if;
END //
delimiter ;
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_date_edition_publisher(published_date Date,selected_edition int, publisher varchar(512))
BEGIN
if selected_edition is null or published_date is null or publisher then
	select 'fields can not be empty';
else
	select * from book where Edition = selected_edition and PublishedDate = published_date and PublisherName = publisher  order by Title;
end if;
END //
delimiter ;
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_title_edition_publisher_date(book_title varchar(512),selected_edition int, publisher varchar(512),published_date Date)
BEGIN
if selected_edition is null or book_title is null or publisher or published_date is null then
	select 'fields can not be empty';
else
	select * from book where Edition = selected_edition and Title = book_title and PublisherName = publisher and PublishedDate = published_date order by Title;
end if;
END //
delimiter ;
-- ----------------------------------------------------------------------------------------------------------------------------------
 -- call  search_by_title_edition_date('Night', 1,2021-02-08);
-- insert into book (Edition,Price,PageNumber,Title,Genre,Type,PublishedDate,PublisherName )
-- 	values(1,1000,100,'Night','drama','student','2021-02-08', 'kevin maccain');

delimiter //
CREATE PROCEDURE borrow(in user_id int,in book_id int, out statement varchar(100))
BEGIN

declare borrow_id int default null;
declare failure boolean default false;
select @user_status := accountSatus, @user_balance := Balance, @pro_date := ProhibitionDate from account where UserId = user_id;
select @book_tag := type , @book_price := Price from book where BookId = book_id;


if @user_status = 'normal' then
	if !(@book_tag = 'general') then
		set @statement = 'you are not authorized';
        set failure = true;
	end if;
else if @user_status = 'student' then
	if @book_tag = 'refrence' then
		set @statement = 'you are not authorized';
        set failure = true;
end if;
end if;
end if;

  --   select count(*) from borrow natural join (select BorrowID from borrowhistory where UserId = user_id ) as result where ReturnDate is null;
if (select count(*) from borrow natural join (select BorrowID from borrowhistory where UserId = user_id and result = 'successfull') as result where ReturnDate is null ) > 0 then
	set @statement = 'you already have the book';
	set failure = true;
end if;
if failure = true then
	select @statement into statement;
	select statement;
end if;
if failure = false then
	if @user_balance < (@book_price * 5/100) then
		select 'balance fault';
		set @statement = 'balance fault';
	else if (select count(*) from stores where BookID = book_id) < 1 then
		select 'book fault';
		set @statement = 'book fault';
	else if (select count(*) from borrow where ReturnDate > DueDate and DueDate < Now() and DueDate > date_sub(now(), interval 2 month)) > 3 then
		if( (@pro_date is not null) and @pro_date < now()) then
			update account set ProhibitionDate = date_add(now(), interval 1 month) where UserId = user_id; 
		end if;
			set @statement = 'prohibition';
            select 'prohibition';
	else
		set @statement = 'successfull';
        select 'successful';
        update stores set BookCount = BookCount-1 where BookID = book_id;
        update account set Balance = Balance-(@book_price * 5/100) where UserId = user_id;
	end if;
end if;
end if;
end if;
    insert into borrow(BookID ,BorrowDate ,DueDate ,ReturnDate ,Price )
		values(book_id,Now(),date_add(now() , interval 40 day),null, (@book_price * 5/100));
	insert into BorrowHistory(BorrowID ,UserId ,result )
		values(book_id,user_id,@statement);
select @statement into statement;
select statement;
END //
delimiter ;
call borrow(5,2);
select * from borrowhistory;
select * from borrow;
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE give_book_back(in user_id int,in book_id int,out statement varchar(100))
BEGIN
select @id := BorrowID from borrow natural join (select BorrowID from borrowhistory where UserId = user_id and result = 'successfull' ) as result where BookID = book_id and ReturnDate is null;
select 'unsuccessfull' into statement;
if((select ReturnDate from borrow where BorrowID = @id) is null) then
update stores set BookCount = BookCount+1 where BookID = book_id;
update borrow set ReturnDate = now() where BorrowID = @id;
select 'successfull' into statement;
end if;

select statement;
END //
delimiter ;
select BorrowID from borrowhistory where UserId = 1 and result = 'successfull';
select *, @id := BorrowID from borrow natural join (select BorrowID from borrowhistory where UserId = 1 and result = 'successfull' ) as result where BookID = 1;
select BorrowID from borrowhistory where UserId = 1 and result = 'successfull';
select * from borrowhistory;
select * from stores;
select * from borrow;
call give_book_back(1,1);

call give_book_back(3,2);
select * from stores;
select * from librarymessage;
-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE increase_balance(user_id int, amount numeric(10,5), out statement varchar(100))
BEGIN
if amount > 0 then
	update account set Balance = Balance+amount where UserId = user_id;
    select 'successfull' into statement;
else
	select 'amount is not valid' into statement;
end if;
select statement;
END //
delimiter ;

-- -----------------------------------------------------------------------------------------------------------------------------------
select * from customer;
call increase_balance(5,10000);
select * from account;

insert into book(Edition,
    Price ,
    PageNumber,
    Title,
    Genre,
    type,
    PublishedDate,
    PublisherName)
values(1,100,100,'title','drama','student','2021-02-03','me');

select * from book;
select * from borrowhistory;
select * from stores;
select * from librarymessage;

-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE add_book(user_id int, selected_edition int,book_price Numeric(10,5),page_number int,book_title varchar(50),book_genre varchar(50),
    tag varchar(100),
    published_date Date,
    publisher varchar(100),
    out statement varchar(100))
BEGIN
select @user_status := accountSatus from account where UserId = user_id;
if !(@user_status = 'librarian' or @user_status = 'manager') then
	select 'you are unauthorized' into statement;
else
	if (select count(*) from book where Title=book_title and Edition=selected_edition) > 0 then
		update stores set BookCount = BookCount+1 where BookId = (select BookID from book where Title = book_title and Edition = selected_edition);
	else
		insert into book(Edition,Price ,PageNumber,Title,Genre,type,PublishedDate,PublisherName)
		values(selected_edition,book_price,page_number,book_title,book_genre,tag,published_date,publisher);
        insert into stores(BookID,BookCount) values(LAST_INSERT_ID(), 1);
	end if;
	select 'successfull' into statement;
end if;
select statement;
END //
delimiter ;


-- ----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE get_success_list(user_id int, page int,out statement varchar(100))
BEGIN
select @user_status := accountSatus from account where UserId = user_id;
if !(@user_status = 'librarian' or @user_status = 'manager') then
	select 'you are unauthorized' into statement;
else
	select 'successfull' into statement;
	SELECT * FROM LibraryMessage  order by CreationDate Desc LIMIT 5 offset page  ;
end if;
select statement;
END //
delimiter ;
-- -----------------------------------------------------------------------------------------------------------------------------------

delimiter //
CREATE PROCEDURE search_by_username(user_id int, user_name varchar(100), page int, out statement varchar(100))
BEGIN
select @user_status := accountSatus from account where UserId = user_id;
if page = null then
	set page = 0;
end if;
if !(@user_status = 'librarian' or @user_status = 'manager') then
	select 'you are unauthorized' into statement;
else
	select 'successfull' into statement;
	select * from customer natural join (select UserId as Id,Balance,CreationDate,UserPassword,Username,accountSatus,ProhibitionDate from account where UserName = user_name) as result order by Lastname LIMIT 5 offset page;
end if;
select statement;
END //
delimiter ;
-- -----------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE search_by_lastname(user_id int, last_name varchar(100), page int, out statement varchar(100))
BEGIN
select @user_status := accountSatus from account where UserId = user_id;
if page = null then
	set page = 0;
end if;
if !(@user_status = 'librarian' or @user_status = 'manager') then
	select 'you are unauthorized' into statement;
else
	select 'successfull' into statement ;
	select * from account natural join (select id as UserId,FirstName,LastName,Address,Phone from customer where LastName = last_name) as result order by LastName LIMIT 5 offset page;
end if;
select statement;
END //
delimiter ;
select * from account natural join (select id as UserId,FirstName,LastName,Address,Phone from customer) as result order by LastName LIMIT 5;
select * from customer natural join (select UserId as Id,Balance,CreationDate,UserPassword,Username,accountSatus,ProhibitionDate from account ) as result order by Lastname LIMIT 5 ;
select * from customer;
-- -----------------------------------------------------------------------------------------------------------------------------------

delimiter //
CREATE PROCEDURE search_history(user_id int,customer_id int,out statement varchar(100))
BEGIN
select @user_status := accountSatus from account where UserId = user_id;
if !(@user_status = 'librarian' or @user_status = 'manager') then
	select 'you are unauthorized' into statement;
else
	select 'successfull' into statement;
	select * from borrowhistory where UserId = customer_id order by CreationDate Desc;
end if;
select statement;
END //
delimiter ;
select * from borrowhistory;

-- -----------------------------------------------------------------------------------------------------------------------------------

delimiter //
CREATE PROCEDURE get_passed_books(user_id int,out statement varchar(100))
BEGIN
select @user_status := accountSatus from account where UserId = user_id;
if !(@user_status = 'librarian' or @user_status = 'manager') then
	select 'you are unauthorized' into statement;
else
	select 'successfull' into statement;
	select Title from book natural join (select BookID,DueDate from borrow where DueDate < now()) as result order by DueDate Desc;
end if;
select  statement;
END //
delimiter ;
-- ---------------------------------------------------------------------------------------------------------------------------------------------------------
delimiter //
CREATE PROCEDURE sign_out(out statement int)
BEGIN
	select -1 into statement;
    select statement;
END //
delimiter ;

-- delimiter //
-- CREATE PROCEDURE get_same_books(user_id int,book_id int,out statement varchar(100))
-- BEGIN
-- select @user_status := accountSatus from account where UserId = user_id;
-- if !(@user_status = 'librarian' or @user_status = 'manager') then
-- 	select 'you are unauthorized' into statement;
-- else
-- 	select 'successfull' into statement;
--     select concat(@m,'book ',Cast(book_id) as char);
--     select instr(Message,@m) into @count;
-- 	select * from librarymessage where order by CreationDate Desc ;

-- end if;
-- select  statement;
-- END //
-- delimiter ;
-- select * from librarymessage;