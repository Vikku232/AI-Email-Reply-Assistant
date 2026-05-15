import React, { useState } from 'react';
import axios from 'axios';
import { Container, MenuItem, Typography, Box, TextField, FormControl, InputLabel, Select, CircularProgress, Button } from '@mui/material';
import './App.css'

function App() {
  const [emailContent, setEmailContent] = useState('');
  const [tone, setTone] = useState('');
  const [generatedReply, setGeneratedReply] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async () => {
    setIsLoading(true);
    setError('');
    try {
      const response = await axios.post('http://localhost:8080/api/email/generate', {
        emailContent,
        tone
      });
      const data = response.data;
      setGeneratedReply(typeof data === 'string' ? data : JSON.stringify(data, null, 2));
    } catch (error) {
      setError('Failed to generate email reply. Please try again.');
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{py:4}}> 
      <Typography variant="h3" component="h1" gutterBottom>
        Email Reply Generator
      </Typography>

      <Box sx={{ mx: 3 }}>
        <TextField
        fullWidth
        multiline
        rows={6}
        variant='outlined'
        label="Original Email Content"
        value={emailContent || ''}
        onChange={(e) => setEmailContent(e.target.value)}
        sx={{ mb: 2 }} />

        <FormControl fullWidth sx={{ mb: 2 }}>
          <InputLabel>Tone (Optional)</InputLabel>
          <Select
            value={tone || ''}
            label={"Tone (Optional)"}
            onChange={(e) => setTone(e.target.value)}>
              <MenuItem value="">None</MenuItem>
              <MenuItem value="professional">Professional</MenuItem>
              <MenuItem value="casual">Casual</MenuItem>
              <MenuItem value="friendly">Friendly</MenuItem>
              <MenuItem value="assertive">Assertive</MenuItem>
          </Select>
        </FormControl>

        <Button
          variant='contained'
          onClick={handleSubmit}
          disabled={!emailContent || isLoading}
          fullWidth>
          {isLoading ? <CircularProgress size={24} /> : 'Generate Reply'}
        </Button>
      </Box>

      {error && (
        <Typography color='error' sx={{ mb:2 }}>
          {error} 
        </Typography>
      )}

      {generatedReply && (
        <Box sx={{ mt: 3 }}>
          <Typography variant="h6" gutterBottom>
            Generated Reply:
          </Typography>
          <TextField
            fullWidth
            multiline
            rows={6}
            variant='outlined'
            value={generatedReply || ''}
            InputProps={{ readOnly: true }}/>

            <Button
              variant='outlined'
              sx={{ mt: 2 }}
              onClick={() => navigator.clipboard.writeText(generatedReply)}>
              Copy to Clipboard
            </Button>
        </Box>
      )}
    </Container>
  )
}

export default App
