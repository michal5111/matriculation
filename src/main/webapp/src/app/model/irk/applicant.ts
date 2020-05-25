import {Document} from './document';
import {Observable} from 'rxjs';

export class Applicant {
  id: number;
  email: string;
  indexNumber: string;
  password: string;
  name: {
    middle: string;
    maiden: string;
    family: string;
        given: string;
    };
    phone: string;
    citizenship: string;
    photo: string;
    image: any;
    image$: Observable<Blob>;
    photoPermission: null;
    casPasswordOverwrite: Boolean;
    modification_date: Date;
    basicData: {
        sex: string;
        pesel: string;
        dateOfBirth: Date;
        cityOfBirth: string;
        countryOfBirth: string;
        dataSource: string;
    };
    contactData: {
        phoneNumber: string;
        phoneNumberType: string;
        phoneNumber2: string;
        phoneNumber2Type: string;
        officialStreet: string;
        officialStreetNumber: string;
        officialFlatNumber: string;
        officialPostCode: string;
        officialCity: string;
        officialCityIsCity: Boolean;
        officialCountry: string;
        realStreet: string;
        realStreetNumber: string;
        realFlatNumber: string;
        realPostCode: string;
        realCity: string;
        realCityIsCity: Boolean;
        realCountry: string;
        modificationDate: Date;
    };
    additionalData: {
        documentType: string;
        documentNumber: string;
        documentExpDate: Date;
        documentCountry: string;
        militaryStatus: string;
        militaryCategory: string;
        wku: string;
        cityOfBirth: string;
        countryOfBirth: string;
        mothersName: string;
        fathersName: string;
    };
    foreignerData: {
      foreigner_status: [string];
      polish_card_issue_date: Date;
      polish_card_valid_to: Date;
      polish_card_number: string;
      polish_card_issue_country: string;
      base_of_stay: string;
    };
    educationData: {
        highSchoolType: string;
        highSchoolName: string;
        highSchoolUsosCode: string;
        highSchoolCity: string;
        documents: [Document];
    };
    usosId: number;
    assignedIndexNumber: number;
}
