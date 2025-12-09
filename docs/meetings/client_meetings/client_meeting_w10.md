# 25-05-12 Client Meeting

|           |                                                              |
| --------- | ------------------------------------------------------------ |
| Time      | 1:00pm - 1:30am                                              |
| Logistic  | Zoom                                                         |
| Attendees | Michelle, Zongliang Han |

## Agenda
1. Share current design layout, user cases and database structure  
2. Discuss prototype design feedback  
3. Clarify data included in the database

## Discussion Summary
| **Time**  | **Notes** | 
|-----------|-----------|
| 1:00pm    | Shared current progress: user cases, low fidelity design, data structure. |
| 1:10pm    | Reviewed prototype: design is acceptable; Need to include the order history filter/search function; Design accessories recommendation layout |
| 1:20pm    | Q&A with included data field:
- SKU is same as productCode in SAP  
- GTIN, UPC, EAN, ISBN are global number for barcode, no need to include.  
- Publish/Visibility, no need to include
- Cross sell, up sell, no need to include
- Sales price, no need, just a banner (optional)

## Action Items

| **Item** | **Action Description** | **Assignee(s)** | **Due Date** |
|----------|------------------------|-----------------|--------------|
| 1        | Update SQL | Backend team | This week |
| 2        | Refine the order history filter/search function | All team | This sprint |
| 3        | Design the accessories recommendation layout | Frontend Team | This sprint |
| 4        | Redesign the order interaction. (one product is not available, others available ) | Frontend Team | This sprint |
| 5        | Think about the sales price layout | Team | This sprint |
| 5        | Think about how to download the data with page layout (option) | Team | This sprint |