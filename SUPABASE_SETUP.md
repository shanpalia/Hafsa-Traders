# Hafsa Traders Supabase Setup

This project now uses **Supabase Auth** for customer/admin login and **Supabase PostgreSQL + RLS** for online order synchronization.

## 1. Create a Supabase project
Copy the project URL and the **anon public key** from Project Settings > API.

## 2. Configure the Android project
Open `gradle.properties` and add:

```properties
SUPABASE_URL=https://YOUR_PROJECT_REF.supabase.co
SUPABASE_ANON_KEY=YOUR_ANON_PUBLIC_KEY
```

For Codemagic, preferably store these as secure environment variables and write them into `gradle.properties` before the build.

## 3. Create database tables and security policies
Open Supabase SQL Editor and run:

`supabase/schema.sql`

## 4. Create customer and admin accounts
- Customers can register directly from the app.
- Create the owner/admin account in Supabase Auth with the same email configured in the app's Admin Settings (default owner email is `admin@hafsatraders.com`).
- Run the final SQL command in `schema.sql` to set that profile's role to `ADMIN`.

## 5. Email confirmation
For the simplest first build, you can disable "Confirm email" in Supabase Auth provider settings. If confirmation is enabled, a new user must confirm the email before logging in.

## Online order flow
1. Customer logs in with Supabase.
2. Customer places order.
3. Order and item rows are pushed to Supabase.
4. Admin logs in with the authorized Supabase owner account and sees online orders.
5. Admin changes RECEIVED / PROCESSING / READY / COMPLETED.
6. Customer and admin apps poll Supabase every 5 seconds and refresh the local UI cache.

The anon key is safe to embed in an app **only when RLS is enabled**. Never put a Supabase `service_role` key in the Android app.
