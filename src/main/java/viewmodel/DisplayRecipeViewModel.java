package viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Ingredient;
import model.IngredientAdapter;
import model.Model;
import model.Recipe;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DisplayRecipeViewModel implements PropertyChangeListener
{
  private final Model model;
  private Recipe recipe;
  private final StringProperty title;
  private final StringProperty author;
  private final ListProperty<IngredientAdapter> ingredientsList;
  private final StringProperty multiplier;
  private final StringProperty description;
  private final StringProperty rate;
  private final StringProperty error;

  public DisplayRecipeViewModel(Model model)
  {
    this.model = model;
    this.recipe = null;
    this.title = new SimpleStringProperty("");
    this.author = new SimpleStringProperty("");
    this.ingredientsList = new SimpleListProperty<>(FXCollections.observableArrayList());
    this.multiplier = new SimpleStringProperty("");
    this.description = new SimpleStringProperty("");
    this.rate = new SimpleStringProperty("");
    this.error = new SimpleStringProperty("");
  }

  public void rate()
  {
    model.rateRecipe(Integer.valueOf(rate.get()), recipe);
  }

  public void addToFavourites()
  {
    this.model.addToFavourites(recipe);
  }

  public void multiplyIngredients()
  {
    try
    {
      if (multiplier.get() != null)
      {
        for (int i = 0; i < recipe.getIngredients().size(); i++)
        {
          IngredientAdapter ingredientAdapter = ingredientsList.get().get(i);
          ingredientAdapter.setAmount(ingredientAdapter.getAmount()*Double.valueOf(multiplier.get()));
          ingredientsList.set(i, ingredientAdapter);
        }
      }
    }
    catch (Exception e)
    {
      error.set("Multiplier has to be a number.");
    }

  }

  public void bindTitle(StringProperty property)
  {
    this.title.bindBidirectional(property);

  }

  public void bindAuthor(StringProperty property)
  {
    this.author.bindBidirectional(property);
  }

  public void bindIngredientsList(ObjectProperty<ObservableList<IngredientAdapter>> property)
  {
    property.bindBidirectional(ingredientsList);
  }

  public void bindMultiplier(StringProperty property)
  {
    this.multiplier.bindBidirectional(property);
  }

  public void bindDescription(StringProperty property)
  {
    this.description.bindBidirectional(property);
  }

  public void bindRate(ReadOnlyObjectProperty<String> property)
  {
    this.rate.bind(property);
  }

  public void bindError(StringProperty property)
  {
    property.bind(error);
  }

  public void reset()
  {
    this.title.set("");
    this.author.set("");
    this.ingredientsList.clear();
    this.description.set("");
    this.error.set("");
    this.recipe = null;
  }

  @Override public void propertyChange(PropertyChangeEvent evt)
  {
    Platform.runLater(() -> {
      if (evt.getPropertyName().equals(SearchRecipesViewModel.RECIPESELECTED))
      {
        this.recipe = (Recipe) evt.getNewValue();
        this.title.set(recipe.getTitle());
        this.author.set(recipe.getUsername());
        for (int i = 0; i < recipe.getIngredients().size(); i++)
        {
          try
          {
            ingredientsList.add(new IngredientAdapter((Ingredient) recipe.getIngredients().get(i).clone()));
          }
          catch (CloneNotSupportedException e)
          {
            throw new RuntimeException(e);
          }
        }
        this.description.set(recipe.getDescription());
        this.error.set("");
      }
    });
  }
}
