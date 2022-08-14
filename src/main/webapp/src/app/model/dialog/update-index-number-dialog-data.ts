export class UpdateIndexNumberDialogData {
  personId: number;
  indexTypeCode: string;
  indexNumber: string;
  organizationalUnitCode: string;

  constructor(personId: number, indexTypeCode: string, indexNumber: string, organizationalUnitCode: string) {
    this.personId = personId;
    this.indexTypeCode = indexTypeCode;
    this.indexNumber = indexNumber;
    this.organizationalUnitCode = organizationalUnitCode;
  }
}
