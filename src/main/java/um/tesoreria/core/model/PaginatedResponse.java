package um.tesoreria.core.model;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> data;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    public PaginatedResponse() {}

    public PaginatedResponse(List<T> data, long totalElements, int totalPages, int currentPage, int pageSize) {
        this.data = data;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
