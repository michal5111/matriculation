class ParameterType {
}

export interface DataSourceAdditionalParameter {
  type: ParameterType;
  name: string;
  value: any;
  placeholderValue: string;
  accept: string | undefined;
  fileTemplate: string | undefined;
  selectionListSource: SelectionListValue[] | undefined;
}

export class SelectionListValue {
  name: string;
  value: string;

  constructor(name: string, value: string) {
    this.name = name;
    this.value = value;
  }
}

export class DataSource {
  name: string;
  id: string;
  additionalParameters: DataSourceAdditionalParameter[];

  constructor(name: string, id: string, additionalParameters?: DataSourceAdditionalParameter[]) {
    this.name = name;
    this.id = id;
    this.additionalParameters = additionalParameters ?? [];
  }
}
