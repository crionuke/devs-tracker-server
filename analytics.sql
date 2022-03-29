-- count users;
select count(*) from users;

-- all users:
select * from users order by u_added desc;

-- user's trackers:
select t_user_id, t_id, t_added, d_name from trackers join developers on t_developer_id = d_id where t_user_id = <user_d>;

-- count trackers per users
select u_id, count(*) as t_count from users join trackers on u_id = t_user_id group by u_id order by t_count desc;