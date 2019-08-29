import { Document } from './document'
import { Observable } from 'rxjs';

export class Applicant {
    id: number;
    email: string;
    index_number: string;
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
    image$: Observable<Blob>
    photo_permission: null;
    cas_password_overwrite: Boolean;
    modification_date: Date;
    basic_data: {
        sex: string;
        pesel: string;
        date_of_birth: Date;
        city_of_birth: string;
        country_of_birth: string;
        data_source: string;
    };
    contact_data: {
        phone_number: string;
        phone_number_type: string;
        phone_number2: string;
        phone_number2_type: string;
        official_street: string;
        official_street_number: string;
        official_flat_number: string;
        official_post_code: string;
        official_city: string;
        official_city_is_city: Boolean;
        official_country: string;
        real_street: string;
        real_street_number: string;
        real_flat_number: string;
        real_post_code: string;
        real_city: string;
        real_city_is_city: Boolean;
        real_country: string;
        modification_date: Date;
    };
    additional_data: {
        document_type: string;
        document_number: string;
        document_exp_date: Date;
        document_country: string;
        military_status: string;
        military_category: string;
        wku: string;
        city_of_birth: string;
        country_of_birth: string;
        mothers_name: string;
        fathers_name: string;
    };
    foreigner_data: string;
    education_data: {
        high_school_type: string;
        high_school_name: string;
        high_school_usos_code: string;
        high_school_city: string;
        documents: [Document]
    }
}
