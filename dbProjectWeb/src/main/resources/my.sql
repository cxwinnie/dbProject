
create table goods_type(
       id int primary key,
       type varchar2(100) not null,
       father_id int
);

create table goods(
       id int primary key,
       name varchar2(100) not null,
       description varchar2(100) not null,
       price number(10) not null,
       goods_type_id int
);