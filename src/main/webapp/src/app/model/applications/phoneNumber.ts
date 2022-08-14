export class PhoneNumber {
  id: number;
  number: string;
  phoneNumberType: string;
  comment: string;


  constructor(id: number, phoneNumber: string, phoneNumberType: string, comment: string) {
    this.id = id;
    this.number = phoneNumber;
    this.phoneNumberType = phoneNumberType;
    this.comment = comment;
  }
}
