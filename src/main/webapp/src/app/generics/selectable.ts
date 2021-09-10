export class Selectable<T> {
  isSelected: boolean;
  value: T;

  constructor(value: T, isSelected: boolean) {
    this.isSelected = isSelected;
    this.value = value;
  }
}
