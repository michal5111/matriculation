export class IdentityDocument {
  country: string;
  expDate: Date;
  number: string;
  type: string;

  constructor(country: string, expDate: Date, documentNumber: string, type: string) {
    this.country = country;
    this.expDate = expDate;
    this.number = documentNumber;
    this.type = type;
  }
}
