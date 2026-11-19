
import React, { useEffect, useState } from "react";
import { Box, Card, CardContent, Typography } from "@mui/material";
import { useNavigate } from "react-router";
import { getActivities } from "../services/api";

const ActivityList = () => {
  const [activities, setActivities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const fetchActivities = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getActivities();
      setActivities(response.data);
    } catch (error) {
      console.error("Error fetching activities:", error);
      setError("Unable to load activities. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchActivities();
  }, []);

  if (loading) {
    return <Typography>Loading activities...</Typography>;
  }

  if (error) {
    return <Typography color="error">{error}</Typography>;
  }

  if (activities.length === 0) {
    return <Typography>No activities found. Add your first activity!</Typography>;
  }

  return (
    <Box
      sx={{
        display: "grid",
        gridTemplateColumns: {
          xs: "1fr",
          sm: "repeat(2, 1fr)",
          md: "repeat(3, 1fr)",
        },
        gap: 2,
      }}
    >
      {activities.map((activity) => (
        <Card
          key={activity.id}
          sx={{ cursor: "pointer" }}
          onClick={() => navigate(`/activities/${activity.id}`)}
        >
          <CardContent>
            <Typography variant="h6">{activity.type}</Typography>
            <Typography>
              Duration: {activity.duration} minutes
            </Typography>
            <Typography>
              Calories: {activity.caloriesBurned}
            </Typography>
          </CardContent>
        </Card>
      ))}
    </Box>
  );
};

export default ActivityList;
