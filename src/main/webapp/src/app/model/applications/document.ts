export class Document {
  certificateType: string;
  certificateTypeCode: string;
  certificateUsosCode: string;
  documentYear: Date;
  documentNumber: string;
  issueDate: Date;
  issueInstitution: string;
  issueInstitutionUsosCode: string;
  issueCity: string;
  comment: string;
  modificationDate: Date;

  constructor(
    certificateType: string,
    certificateTypeCode: string,
    certificateUsosCode: string,
    documentYear: Date,
    documentNumber: string,
    issueDate: Date,
    issueInstitution: string,
    issueInstitutionUsosCode: string,
    issueCity: string,
    comment: string,
    modificationDate: Date) {
    this.certificateType = certificateType;
    this.certificateTypeCode = certificateTypeCode;
    this.certificateUsosCode = certificateUsosCode;
    this.documentYear = documentYear;
    this.documentNumber = documentNumber;
    this.issueDate = issueDate;
    this.issueInstitution = issueInstitution;
    this.issueInstitutionUsosCode = issueInstitutionUsosCode;
    this.issueCity = issueCity;
    this.comment = comment;
    this.modificationDate = modificationDate;
  }
}
