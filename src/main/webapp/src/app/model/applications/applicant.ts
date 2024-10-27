import {Document} from './document';
import {Observable} from 'rxjs';
import {Address} from './address';
import {PhoneNumber} from './phoneNumber';
import {IdentityDocument} from './identityDocument';
import {PotentialDuplicateStatus} from '../import/potential-duplicate-status.enum';

export class Applicant {
  id: number;
  foreignId: number;
  email: string;
  indexNumber: string;
  password: string;
  middle: string;
  maiden: string;
  family: string;
  given: string;
  phone: string;
  citizenship: string;
  photo: string;
  image: any;
  image$: Observable<Blob>;
  photoPermission: null;
  modification_date: string;
  sex: string;
  pesel: string;
  dateOfBirth: string;
  cityOfBirth: string;
  countryOfBirth: string;
  dataSource: string;
  addresses: Address[];
  phoneNumbers: PhoneNumber[];
  documentType: string;
  documentNumber: string;
  documentExpDate: string;
  documentCountry: string;
  militaryStatus: string;
  militaryCategory: string;
  wku: string;
  mothersName: string;
  fathersName: string;
  foreignerData: {
    foreigner_status: string[];
    polish_card_issue_date: string;
    polish_card_valid_to: string;
    polish_card_number: string;
    polish_card_issue_country: string;
    base_of_stay: string;
  };
  highSchoolType: string;
  highSchoolName: string;
  highSchoolUsosCode: string;
  highSchoolCity: string;
  documents: Document[];
  identityDocuments: IdentityDocument[];
  primaryIdentityDocument: IdentityDocument;
  usosId: number;
  assignedIndexNumber: number;
  dataSourceId: number;
  uid: string;
  potentialDuplicateStatus: PotentialDuplicateStatus;

  constructor(
    id: number,
    foreignId: number,
    email: string,
    indexNumber: string,
    password: string,
    middle: string,
    maiden: string,
    family: string,
    given: string,
    phone: string,
    citizenship: string,
    photo: string,
    image: any, image$: Observable<Blob>,
    photoPermission: null,
    modificationDate: string,
    sex: string,
    pesel: string,
    dateOfBirth: string,
    cityOfBirth: string,
    countryOfBirth: string,
    dataSource: string,
    addresses: Address[],
    phoneNumbers: PhoneNumber[],
    documentType: string,
    documentNumber: string,
    documentExpDate: string,
    documentCountry: string,
    militaryStatus: string,
    militaryCategory: string,
    wku: string,
    mothersName: string,
    fathersName: string,
    foreignerData: {
      foreigner_status: string[];
      polish_card_issue_date: string;
      polish_card_valid_to: string;
      polish_card_number: string;
      polish_card_issue_country:
        string; base_of_stay: string
    },
    highSchoolType: string,
    highSchoolName: string,
    highSchoolUsosCode: string,
    highSchoolCity: string,
    documents: Document[],
    identityDocuments: IdentityDocument[],
    primaryIdentityDocument: IdentityDocument,
    usosId: number,
    assignedIndexNumber: number,
    dataSourceId: number,
    uid: string,
    potentialDuplicateStatus: PotentialDuplicateStatus) {
    this.id = id;
    this.foreignId = foreignId;
    this.email = email;
    this.indexNumber = indexNumber;
    this.password = password;
    this.middle = middle;
    this.maiden = maiden;
    this.family = family;
    this.given = given;
    this.phone = phone;
    this.citizenship = citizenship;
    this.photo = photo;
    this.image = image;
    this.image$ = image$;
    this.photoPermission = photoPermission;
    this.modification_date = modificationDate;
    this.sex = sex;
    this.pesel = pesel;
    this.dateOfBirth = dateOfBirth;
    this.cityOfBirth = cityOfBirth;
    this.countryOfBirth = countryOfBirth;
    this.dataSource = dataSource;
    this.addresses = addresses;
    this.phoneNumbers = phoneNumbers;
    this.documentType = documentType;
    this.documentNumber = documentNumber;
    this.documentExpDate = documentExpDate;
    this.documentCountry = documentCountry;
    this.militaryStatus = militaryStatus;
    this.militaryCategory = militaryCategory;
    this.wku = wku;
    this.mothersName = mothersName;
    this.fathersName = fathersName;
    this.foreignerData = foreignerData;
    this.highSchoolType = highSchoolType;
    this.highSchoolName = highSchoolName;
    this.highSchoolUsosCode = highSchoolUsosCode;
    this.highSchoolCity = highSchoolCity;
    this.documents = documents;
    this.identityDocuments = identityDocuments;
    this.primaryIdentityDocument = primaryIdentityDocument;
    this.usosId = usosId;
    this.assignedIndexNumber = assignedIndexNumber;
    this.dataSourceId = dataSourceId;
    this.uid = uid;
    this.potentialDuplicateStatus = potentialDuplicateStatus;
  }
}
