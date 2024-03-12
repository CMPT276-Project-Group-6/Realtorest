# **Realtorest**
A small web application used to host a website. Designed for hosting a personal website for realtors. Exploration, sorting, and direct contact functionalities are available for visiting users, while enabling Admin to manage listings and engage with potential buyers effectively, all within a user-friendly, ad-free environment.


## FAQ
### Port Number
The port number of this project have been changed to 9090. This means that the project will be running on `http://localhost:9090/`, which is where you can access the website when hosting locally.


## Notes for Marking This
### Port ID
The port when deploying this project to `localhost` have been changed to `9090`. This is because `8080` is being used by a local LLM that have the port number hardcoded in.

### Environment Variables
Environment variables are being used to store the database credentials.

As shown in `src\main\resources\application.properties`, the environment variables are `${DB_REALTOREST_URL}`, `${DB_REALTOREST_USER}`, and `${DB_REALTOREST_PASS}`.

Using `cdimascio/dotenv-java`, the data is being read from the file `etc\secrets\.env`, which should be provided as a part of the submission. If there are any issues, please contact me at <kzcheng@sfu.ca>.

### Commits with Descriptions
Some commits may have more details in descriptions. They may be useful to provide more insight.

### More Insight into my Workflow Available
If you are interested in seeing more of my workflow, feel free to check out the [GitHub Issue page](https://github.com/kzcheng/CMPT276-Assignment-2/issues?q=) and the [GitHub Projects](https://github.com/users/kzcheng/projects/2) page of this assignment.

Work that are done will no longer be open, so remember to check not just the open Issues.


## About this project
### Abstract
**Realtorest** is a real estate web application that is created for a realtor. The application displays a list of properties that can be sorted based on price, location and room features, and the available map feature can assist users to explore properties in different locations. From general users’ perspective, users can create an account, manage their basic information, control their property choices with features such as ‘Save as Favorite’, and connect with the website owner to discuss or set up an appointment. The application is specifically tailored for our client, who is the middleman that helps property sellers and buyers connect with each other. This application focuses on helping the client upload and display information of properties that are being sold and help potential buyers who are interested in those properties to make the purchase.

### Project Description
Our client is a realtor in British Columbia who does not have a personal website for their real estate business, and due to the competitiveness in the industry, a personal website can benefit our client a lot by helping them connect with potential clients and widening their business scope. Hence, the web application **Realtorest** is created to help our client connect to property buyers and sellers. The description will outline two types of users that will use this software: the project’s client, who will be the admin of the website. And the general customers, who are home buyers, real estate investors, renters, and real estate professionals. Detailed below are the high-level descriptions of the features that **Realtorest** has to offer.   

General customers can explore properties without the sign in but must register to contact the client or to save the properties to the favorites. Sign up/sign in  can be done using email or through third party accounts like Google. This process will save log in details in the database to avoid scam and enable property tracking. The website will also offer filters for location, room numbers, and sorting by price or distance.

**Realtorest** also utilizes application programming interface (API) for property listing view, which allows customers to view properties as markers on a map; this process requires an external API, for example Google Maps, in order to function. Moreover, customers who wish to get updated frequently about real estate news by the website owner also can sign up to be in the automated mailing list that uses an external API allowing the admins to send the update emails automatically. 

As for the Admin, they will be able to login to their website under admin login, which allows them to manage the property listing, by uploading new properties information or removing any properties that they wishes to. In addition, the website owner can get notified by any customers who are wishing to contact them regarding a property.

### Project Competitiveness
There are many personalized websites tailored for realtors, hence it is very competitive for the project to have its own distinctive features that stand out from the rest of the other websites. However, **Realtorest** is different from many other realtor websites in which it does not have any advertisements, because the project is focused on the client, rather than profit like the others. In addition, under UI perspective, most of the realtors websites that the team encountered are usually very hard to navigate and not user friendly, since there are many website features and interactions that all appear at once. Hence, to avoid this problem, **Realtorest** will focus on the customer interaction experience by creating a minimalist layout that can help the user to navigate with ease, while also providing sufficient features. 


## Screenshots
### Home Page
![Home Page](<Documentation/Screenshots/I1 Home Page.png>)
### Properties Listing View
![Properties Listing View](<Documentation/Screenshots/I1 Listing.png>)


## Group Members
### Kevin
GitHub: https://github.com/kzcheng
 
### Nam
GitHub: https://github.com/namneyugn21

### Drishty
GitHub: https://github.com/drishty02

### Amrit
GitHub: https://github.com/htoor1999

### Malaika
GitHub: https://github.com/MalaikaQ


## Documentation
### Proposal
https://docs.google.com/document/d/18ciUDUsWcUADZHcMQhobZ8FYAu0NfuLscaJnMyYtte0

### Requirements and Specification Document - Iteration 1
https://docs.google.com/document/d/1_oOKkc0if6xlwWooyd694t9nAQmbeQBu31ZJ956VdmE/edit?usp=sharing


## Useful Links
### GitHub Repository
https://github.com/CMPT276-Project-Group-6/Realtorest

### GitHub Organization
https://github.com/CMPT276-Project-Group-6

### Canvas Group
https://canvas.sfu.ca/groups/310253

### Canvas Group Project Description
https://canvas.sfu.ca/courses/83043/pages/group-project-description-2?module_item_id=3214720

### Canvas Requirements Document Overview
https://canvas.sfu.ca/courses/83043/files/23085487?module_item_id=3214721

### Canvas Iteration 1
https://canvas.sfu.ca/courses/83043/assignments/952051

### Canvas Customer Commitment Form
https://canvas.sfu.ca/courses/83043/files/23292672?module_item_id=3242357
