insert into USER
values (1, 'casuser');
insert into ROLE
values ('ROLE_ADMIN', 'Superadministrator');
insert into USERROLE
values (1, 'ROLE_ADMIN');

insert into ROLE
values ('IMPORT_PROGRESS', 'Oglądanie progresu');
insert into ROLE
values ('IMPORT_SAVE', 'Uruchamianie zapisywania w usosie');
insert into ROLE
values ('IMPORT_DATA_SOURCES', 'Wyświetlanie listy źródeł danych');
insert into ROLE
values ('IMPORT_ARCHIVE', 'Archiwizacja importów');
insert into ROLE
values ('IMPORT_CHANGE_INDEX', 'Zmiana indeksów');
insert into ROLE
values ('USOS_DICTIONARIES', 'Dostęp do słowników usos');
insert into ROLE
values ('IMPORT_VIEW_APPLICATIONS', 'Oglądanie zgłoszeń');
insert into ROLE
values ('IMPORT_IMPORT_APPLICATIONS', 'Uruchamianie importu zgłoszeń');
insert into ROLE
values ('IMPORT_VIEW', 'Oglądanie importów');
insert into ROLE
values ('IMPORT_CREATE', 'Tworzenie importów');
insert into ROLE
values ('IMPORT_DELETE', 'Usuwanie importów');
insert into ROLE
values ('IMPORT_LDAP', 'Przeglądanie Active directory (Uidów)');
