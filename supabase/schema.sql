-- HAFSA TRADERS: Supabase Auth + online order system
-- Run this in Supabase SQL Editor.

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  email text not null default '',
  role text not null default 'CUSTOMER' check (role in ('CUSTOMER','ADMIN')),
  created_at timestamptz not null default now()
);

create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer set search_path = public
as $$
begin
  insert into public.profiles(id, email, role)
  values (new.id, coalesce(new.email,''), 'CUSTOMER')
  on conflict (id) do update set email = excluded.email;
  return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
after insert on auth.users
for each row execute procedure public.handle_new_user();

create table if not exists public.orders (
  id text primary key,
  order_number text not null unique,
  user_id uuid not null references auth.users(id) on delete cascade,
  customer_name text not null,
  customer_phone text not null,
  customer_email text not null default '',
  customer_address text not null default '',
  total_amount double precision not null,
  subtotal double precision not null,
  discount double precision not null default 0,
  payment_status text not null,
  payment_method text not null,
  payment_ref text not null default '',
  order_status text not null default 'RECEIVED',
  special_instructions text not null default '',
  created_at bigint not null,
  updated_at bigint not null
);

create table if not exists public.order_items (
  id text primary key,
  order_id text not null references public.orders(id) on delete cascade,
  item_id text not null,
  item_name text not null,
  unit_price double precision not null,
  unit text not null,
  quantity integer not null,
  subtotal double precision not null
);

create table if not exists public.order_status_history (
  id text primary key,
  order_id text not null references public.orders(id) on delete cascade,
  status text not null,
  message text not null,
  changed_at bigint not null
);

alter table public.profiles enable row level security;
alter table public.orders enable row level security;
alter table public.order_items enable row level security;
alter table public.order_status_history enable row level security;

-- Profiles
create policy "profile owner read" on public.profiles for select using (auth.uid() = id);
create policy "admin read profiles" on public.profiles for select using (
  exists(select 1 from public.profiles p where p.id = auth.uid() and p.role = 'ADMIN')
);

-- Orders: customer only sees own; ADMIN sees and manages all.
create policy "customer read own orders" on public.orders for select using (auth.uid() = user_id);
create policy "customer create own orders" on public.orders for insert with check (auth.uid() = user_id);
create policy "admin read all orders" on public.orders for select using (
  exists(select 1 from public.profiles p where p.id = auth.uid() and p.role = 'ADMIN')
);
create policy "admin update orders" on public.orders for update using (
  exists(select 1 from public.profiles p where p.id = auth.uid() and p.role = 'ADMIN')
);

-- Order items
create policy "customer read own items" on public.order_items for select using (
  exists(select 1 from public.orders o where o.id = order_id and o.user_id = auth.uid())
);
create policy "customer add own items" on public.order_items for insert with check (
  exists(select 1 from public.orders o where o.id = order_id and o.user_id = auth.uid())
);
create policy "admin read all items" on public.order_items for select using (
  exists(select 1 from public.profiles p where p.id = auth.uid() and p.role = 'ADMIN')
);

-- Tracking history
create policy "customer read own history" on public.order_status_history for select using (
  exists(select 1 from public.orders o where o.id = order_id and o.user_id = auth.uid())
);
create policy "customer add initial history" on public.order_status_history for insert with check (
  exists(select 1 from public.orders o where o.id = order_id and o.user_id = auth.uid())
);
create policy "admin read all history" on public.order_status_history for select using (
  exists(select 1 from public.profiles p where p.id = auth.uid() and p.role = 'ADMIN')
);
create policy "admin add tracking history" on public.order_status_history for insert with check (
  exists(select 1 from public.profiles p where p.id = auth.uid() and p.role = 'ADMIN')
);

-- After creating the owner account in Supabase Auth, promote it to ADMIN:
-- update public.profiles set role = 'ADMIN' where email = 'YOUR_ADMIN_EMAIL@example.com';
