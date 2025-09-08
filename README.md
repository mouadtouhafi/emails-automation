# 📧 Job Mail Sender Automation

This project automates the process of **navigating to job post links**, **extracting job details**, **generating personalized job application emails using AI**, and **sending them automatically to the recruiter via Gmail** with attachments (CV + motivation letter).

---

## 🚀 Features
- ✅ Reads job post links from `job_links.txt`
- ✅ Uses **Selenium (EdgeDriver)** to open job links and extract job post text
- ✅ Calls **Hugging Face API (DeepSeek model)** to generate professional job application emails (English or French)
- ✅ Generates structured emails in **JSON format**
- ✅ Sends emails with **attachments (CV & cover letter)** via Gmail (using Jakarta Mail)
- ✅ Handles popups automatically on LinkedIn job posts
- ✅ Secure handling of **tokens and passwords** (read from local `.txt` files)
