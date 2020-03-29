export interface Page<T> {
    count: number;
    next: String;
    previous: String;
    results: [T]
}
