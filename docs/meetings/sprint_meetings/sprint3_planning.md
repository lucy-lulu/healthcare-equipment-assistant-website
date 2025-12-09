# Sprint Planning Meeting Documentation

## Meeting Details
- **Date:** 02/05/2025  
- **Duration:** 60 mins  
- **Participants:**  
  * Zongliang Han  
  * Shuran Yang  
  * Shenyi Hu  
  * Tingyu Wang  
  * Wenxu Guo  
  * Luyun Li  
  * Jiale Xu  
  * Chan Tang  
  * Zihao Wang  
  * Ning Yang  
  * Xinze Li  
  * Zixun Qiu  

---

## Sprint Overview
- **Sprint Number:** 3  
- **Sprint Duration:** 02/05/2025 – 22/05/2025  
- **Sprint Goals:**  
  - Finalise and confirm UI prototype with client  
  - Implement core product features for business partners and Novis staff  
  - Design and build a MySQL database schema **based solely on the exported product and category data** provided (since no WordPress copy or SAP sandbox is available)  
  - Begin access control middleware development  
  - Conduct initial unit testing and feedback-based iteration  

---

## Sprint 3 Planning Checklist

| Sprint Goal                   | Meeting ToDo                                           | Follow-up                                  |
|-------------------------------|--------------------------------------------------------|--------------------------------------------|
| UI prototype finalisation      | Confirm screens and interactions with client           | Revise designs based on final feedback     |
| Core feature implementation    | Split frontend/backend tasks for product search, detail, quote | Track progress in GitHub project board     |
| Custom database design         | Analyse exported data and design matching MySQL schema | Update ER diagrams and generate migration scripts |
| Access control middleware      | Draft role-based permission logic                      | Implement basic role checks in backend     |
| Testing and iteration          | Start writing unit tests for implemented components    | Collect feedback and make improvements     |

---

## Sprint 3 Action Items

### UI Prototype & Client Confirmation
- Walk through final Figma prototype with client  
- Gather confirmation or final tweaks  
- Prepare approved design assets for development  

### Frontend Feature Development
- Implement search/filter products by name, feature, category  
- Build product detail view accessible to business partners  
- Create frontend forms for sending quotes to Novis  

### Backend Feature Development
- Analyse provided export data to design custom MySQL schema  
- Implement database and load real product data  
- Implement quote creation, tracking, and access control logic  

### Testing & Quality Assurance
- Write initial unit tests for backend modules  
- Conduct frontend testing on product search and display  
- Gather feedback during sprint reviews and iterate  

---

## Dependencies/Requirements

- Confirmed product backlog and prototype design  
- Exported product and category data from client  
- No direct access to WordPress copy or SAP sandbox (work only with exports)  

---

## Risks and Mitigation

- **Risk 1:** Exported data may be incomplete or require cleaning  
  **Mitigation Strategy:** Allocate time for data analysis and preprocessing; raise issues early with client  

- **Risk 2:** Designing the custom database without official SAP or WordPress schema may lead to rework  
  **Mitigation Strategy:** Keep schema modular and flexible; document assumptions clearly for client feedback  

- **Risk 3:** Integration delays due to parallel frontend-backend development  
  **Mitigation Strategy:** Maintain clear API definitions and regular cross-team check-ins  
