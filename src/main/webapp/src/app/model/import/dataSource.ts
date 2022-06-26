class ParameterType {
}

export interface DataSourceAdditionalParameter {
  type: ParameterType;
  name: string;
  value: any;
  placeholderValue: string;
}

export class DataSource {
  name: string;
  id: string;
  additionalParameters: DataSourceAdditionalParameter[];
}
