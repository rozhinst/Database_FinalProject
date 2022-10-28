create table  Book(
	BookID int auto_increment,
    Edition int default 1,
    Price Numeric(10,5),
    PageNumber int,
    Title varchar(50),
    Genre varchar(50),
    type varchar(100),
    PublishedDate Date,
    PublisherName varchar(100),
    primary key (BookId,Edition)
);

create table Stores(
	BookID int,
    BookCount int,
    primary key (BookID),
    foreign key (BookID) references Book(BookID)  on delete cascade on update cascade
);

create table Customer(
	Id int auto_increment,
    FirstName varchar(100),
    LastName varchar(100),
    Address varchar(512),
    Phone char(11),
    primary key (Id)
);

create table Account(
	UserId int,
	Balance Numeric(10,5),
	CreationDate Date,
    UserPassword varchar (512),
    Username varchar(100) ,
    accountSatus varchar(10),
    ProhibitionDate Date default null,
    primary key (UserId),
    foreign key (UserId) references Customer(Id) on delete cascade on update cascade
);

create table Borrow(
	BorrowID int auto_increment,
	BookID int,
    BorrowDate Date,
    DueDate Date,
    ReturnDate Date,
    Price Numeric(5,5),
    primary key (BorrowID),
    foreign key (BookID) references  Book(BookID)  on delete cascade on update cascade
);

create table BorrowHistory(
	Id int auto_increment,
	BorrowID int,
	UserId int,
    result varchar(100),
    ProhibitionDate date default null,
    CreationDate Date default now(),
    primary key (Id),
    foreign key (BorrowID) references  Borrow(BorrowID)  on delete cascade on update cascade,
    foreign key (UserId) references  Customer(Id) on delete cascade on update cascade
);

create table LibraryMessage(
Id int auto_increment,
Message varchar(512),
CreationDate Date default now(),
primary key(Id)
);

