export class Address {
  id: number;
  addressType: string;
  city: string;
  cityIsCity: string;
  countryCode: string;
  flatNumber: string;
  postalCode: string;
  street: string;
  streetNumber: string;

  constructor(id: number, addressType: string, city: string, cityIsCity: string, countryCode: string, flatNumber: string, postalCode: string, street: string, streetNumber: string) {
    this.id = id;
    this.addressType = addressType;
    this.city = city;
    this.cityIsCity = cityIsCity;
    this.countryCode = countryCode;
    this.flatNumber = flatNumber;
    this.postalCode = postalCode;
    this.street = street;
    this.streetNumber = streetNumber;
  }
}
