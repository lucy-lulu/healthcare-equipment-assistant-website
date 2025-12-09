# 25-04-15 Client Meeting

|           |                                                              |
| --------- | ------------------------------------------------------------ |
| Time      | 10:30am - 11:30pm                                            |
| Logistic  | Online                                                    |
| Attendees | Jim Mckinlay(client), Michelle(project designer), Zongliang Han, Luyun Li, Zihao Wang, Ning Yang|

## Agenda
1. Share team's current understanding of system roles and flow
2. Discuss prepared question list (user roles, SAP data, app/web priority, etc.)
3. Confirm system priorities and integration boundaries
4. Clarify SAP access, data sources, and notification requirements

## Discussion Summary
| **Time**  | **Notes** | 
|-----------|-----------|
| 10:30am   | Team introduced current understanding on system flow: four user personas (Sales Partner, NOVIS Sales, OT, End User), quote request system between partner and NOVIS. |
| 10:35am   | Jim confirmed web-based system is the priority, with responsive design. Mobile app is not required in this phase. Consider future extension to app. |
| 10:45am   | OT functionality is secondary. Focus first on partner and NOVIS sales workflow. OT features (e.g. compare/recommend products) may come later is time allowed. |
| 10:50am   | Inventory and pricing data are sourced from SAP and wordpress. Sandbox access will be provided(wait confirm from IT manager). Descriptions and images are stored in WordPress, not SAP. |
| 11:00am   | SAP permissions (e.g. updating inventory, creating new tables/objects) need confirmation from IT Manager, use of SAP need to ask Geoff. |
| 11:10am   | Notifications via email are sufficient for now. No push/app notifications needed at this stage. |
| 11:20am   | Required a question list to ask IT manager, get back to the team by the end of 4.16 |

## Action Items

| **Item** | **Action Description** | **Assignee(s)** | **Due Date** |
|----------|------------------------|-----------------|--------------|
| 1        | Follow up with IT Manager Nick and Geoff on SAP access rights, sandbox login, and object creation permissions | Luyun Li, Zongliang Han, Zihao Wang, Ning Yang | This Week |
| 2        | Confirm data handling strategy for SAP (partial data, integration with WordPress) | Backend Team | This Week |
| 3        | Start system design focusing on partner-quote workflow, prioritising web version | Frontend & Backend Teams | Sprint 3 Start |
| 4        | Prepare follow-up questions for next client meeting based on SAP responses | Team | Before next client meeting |
